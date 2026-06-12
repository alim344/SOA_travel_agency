FROM golang:1.25-alpine

WORKDIR /app
COPY . .

RUN go mod tidy
RUN go mod download
RUN go build -o main ./cmd/main

EXPOSE 8000
CMD ["./main"]