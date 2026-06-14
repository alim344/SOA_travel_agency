package tracing

import (
	"context"
	"log"
	"os"

	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/exporters/otlp/otlptrace/otlptracegrpc"
	"go.opentelemetry.io/otel/exporters/stdout/stdouttrace"
	"go.opentelemetry.io/otel/sdk/resource"
	sdktrace "go.opentelemetry.io/otel/sdk/trace"
	semconv "go.opentelemetry.io/otel/semconv/v1.21.0"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

const ServiceName = "api-gateway"

func InitTracer() (*sdktrace.TracerProvider, error) {
	url := os.Getenv("JAEGER_ENDPOINT")
	if len(url) > 0 {
		return initJaegerTracer(url)
	}
	return initFileTracer()
}

func initFileTracer() (*sdktrace.TracerProvider, error) {
	log.Println("Initializing tracing to traces.json")
	f, err := os.Create("traces.json")
	if err != nil {
		return nil, err
	}
	exporter, err := stdouttrace.New(
		stdouttrace.WithWriter(f),
		stdouttrace.WithPrettyPrint(),
	)
	if err != nil {
		return nil, err
	}
	tp := sdktrace.NewTracerProvider(
		sdktrace.WithBatcher(exporter),
		sdktrace.WithSampler(sdktrace.AlwaysSample()),
	)
	otel.SetTracerProvider(tp)
	return tp, nil
}

func initJaegerTracer(url string) (*sdktrace.TracerProvider, error) {
	log.Printf("Initializing tracing to Jaeger at %s\n", url)

	conn, err := grpc.NewClient(url,
		grpc.WithTransportCredentials(insecure.NewCredentials()),
	)
	if err != nil {
		return nil, err
	}

	exporter, err := otlptracegrpc.New(context.Background(),
		otlptracegrpc.WithGRPCConn(conn),
	)
	if err != nil {
		return nil, err
	}

	tp := sdktrace.NewTracerProvider(
		sdktrace.WithBatcher(exporter),
		sdktrace.WithSampler(sdktrace.AlwaysSample()),
		sdktrace.WithResource(resource.NewWithAttributes(
			semconv.SchemaURL,
			semconv.ServiceNameKey.String(ServiceName),
		)),
	)
	otel.SetTracerProvider(tp)
	return tp, nil
}
