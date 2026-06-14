package handler

import (
	"bytes"
	"context"
	"fmt"
	"io"
	"log"
	"net/http"
	"strconv"
	"strings"

	"api-gateway/internal/tracing"
	pb "api-gateway/proto"

	"github.com/gin-gonic/gin"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"google.golang.org/grpc/status"

	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/codes"
)

type GatewayHandler struct {
	stakeholdersServiceURL string
	blogServiceURL         string
	tourServiceURL         string
	followerServiceURL     string
	tourGrpcClient         pb.TourServiceClient
	purchaseGrpcClient     pb.PurchaseServiceClient
	tourExecutionClient    pb.TourExecutionServiceClient
}

func NewGatewayHandler(stakeholdersServiceURL, blogServiceURL, tourServiceURL, followerServiceURL string, tourGrpcAddr string, purchaseGrpcAddr string) *GatewayHandler {

	conn, err := grpc.NewClient(tourGrpcAddr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("Failed to connect to tour-service gRPC: %v", err)
	}

	purchaseConn, err := grpc.NewClient(purchaseGrpcAddr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("Failed to connect to purchase-service gRPC: %v", err)
	}

	return &GatewayHandler{
		stakeholdersServiceURL: stakeholdersServiceURL,
		blogServiceURL:         blogServiceURL,
		tourServiceURL:         tourServiceURL,
		followerServiceURL:     followerServiceURL,
		tourGrpcClient:         pb.NewTourServiceClient(conn),
		purchaseGrpcClient:     pb.NewPurchaseServiceClient(purchaseConn),
		tourExecutionClient:    pb.NewTourExecutionServiceClient(conn),
	}
}

func (h *GatewayHandler) ProxyToStakeholders(c *gin.Context) {
	targetURL := h.stakeholdersServiceURL + c.Request.URL.Path
	log.Printf("[Stakeholders] %s %s", c.Request.Method, targetURL)
	h.proxyRequest(c, targetURL)
}

func (h *GatewayHandler) ProxyToBlog(c *gin.Context) {
	targetURL := h.blogServiceURL + c.Request.URL.Path
	log.Printf("[Blog] %s %s", c.Request.Method, targetURL)
	h.proxyRequest(c, targetURL)
}

func (h *GatewayHandler) ProxyToFollower(c *gin.Context) {
	path := strings.TrimPrefix(c.Request.URL.Path, "/follower")
	targetURL := h.followerServiceURL + path
	log.Printf("[Follower] %s %s", c.Request.Method, targetURL)
	h.proxyRequest(c, targetURL)
}

func (h *GatewayHandler) ProxyToTours(c *gin.Context) {
	if userID, exists := c.Get("userID"); exists {
		c.Request.Header.Set("X-User-ID", fmt.Sprintf("%d", userID.(int)))
	}
	if userRole, exists := c.Get("userRole"); exists {
		c.Request.Header.Set("X-User-Role", userRole.(string))
	}
	targetURL := h.tourServiceURL + c.Request.URL.Path
	log.Printf("[Tours] %s %s", c.Request.Method, targetURL)
	h.proxyRequest(c, targetURL)
}

func (h *GatewayHandler) GetTourByIdGrpc(c *gin.Context) {
	idStr := c.Param("id")
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid tour ID"})
		return
	}

	resp, err := h.tourGrpcClient.GetTourById(context.Background(), &pb.GetTourByIdRequest{Id: id})
	if err != nil {
		log.Printf("[gRPC ERROR] GetTourById: %v", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, resp)
}

func (h *GatewayHandler) CreateTourGrpc(c *gin.Context) {
	var body struct {
		Name        string   `json:"name"`
		Description string   `json:"description"`
		Difficulty  int32    `json:"difficulty"`
		Tags        []string `json:"tags"`
		AuthorId    int64    `json:"author_id"`
	}

	if err := c.ShouldBindJSON(&body); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request body"})
		return
	}

	resp, err := h.tourGrpcClient.CreateTour(context.Background(), &pb.CreateTourRequest{
		Name:        body.Name,
		Description: body.Description,
		Difficulty:  body.Difficulty,
		Tags:        body.Tags,
		AuthorId:    body.AuthorId,
	})
	if err != nil {
		log.Printf("[gRPC ERROR] CreateTour: %v", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, resp)
}

func (h *GatewayHandler) CheckPositionGrpc(c *gin.Context) {
	idStr := c.Param("executionId")
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid execution ID"})
		return
	}

	var body struct {
		Latitude  float64 `json:"latitude"`
		Longitude float64 `json:"longitude"`
	}
	if err := c.ShouldBindJSON(&body); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request body"})
		return
	}

	resp, err := h.tourExecutionClient.CheckPosition(context.Background(), &pb.CheckPositionRequest{
		ExecutionId: id,
		Latitude:    body.Latitude,
		Longitude:   body.Longitude,
	})
	if err != nil {
		log.Printf("[gRPC ERROR] CheckPosition: %v", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"id":                         resp.ExecutionId,
		"touristId":                  resp.TouristId,
		"tourId":                     resp.TourId,
		"status":                     resp.Status,
		"startTime":                  resp.StartTime,
		"endTime":                    resp.EndTime,
		"lastActivityDateTime":       resp.LastActivityDateTime,
		"lastLatitude":               resp.LastLatitude,
		"lastLongitude":              resp.LastLongitude,
		"completedKeyPointsWithTime": resp.CompletedKeyPointsWithTime,
	})
}

func (h *GatewayHandler) AddToCartGrpc(c *gin.Context) {

	ctx, span := otel.Tracer(tracing.ServiceName).Start(c.Request.Context(), "AddToCart")
	defer span.End()

	touristID, ok := getUserID(c)
	if !ok {
		return
	}

	span.SetAttributes(attribute.Int64("tourist.id", touristID))

	var body struct {
		TourId int64 `json:"tour_id"`
	}
	if err := c.ShouldBindJSON(&body); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, "Invalid request body")
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request body"})
		return
	}

	span.SetAttributes(attribute.Int64("tour.id", body.TourId))

	resp, err := h.purchaseGrpcClient.AddToCart(ctx, &pb.AddToCartRequest{
		TouristId: touristID,
		TourId:    body.TourId,
	})
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		if st, ok := status.FromError(err); ok {
			c.JSON(http.StatusBadRequest, gin.H{"error": st.Message()})
		} else {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		}
		return
	}

	span.SetStatus(codes.Ok, "Tour added to cart")
	c.JSON(http.StatusOK, resp)
}

func (h *GatewayHandler) GetCartGrpc(c *gin.Context) {
	ctx, span := otel.Tracer(tracing.ServiceName).Start(c.Request.Context(), "GetCart")
	defer span.End()

	touristID, ok := getUserID(c)
	if !ok {
		return
	}

	span.SetAttributes(attribute.Int64("tourist.id", touristID))

	resp, err := h.purchaseGrpcClient.GetCart(ctx, &pb.GetCartRequest{
		TouristId: touristID,
	})
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	span.SetStatus(codes.Ok, "Cart retrieved successfully")
	c.JSON(http.StatusOK, resp)
}

func (h *GatewayHandler) RemoveFromCartGrpc(c *gin.Context) {
	ctx, span := otel.Tracer(tracing.ServiceName).Start(c.Request.Context(), "RemoveFromCart")
	defer span.End()

	touristID, ok := getUserID(c)
	if !ok {
		return
	}
	span.SetAttributes(attribute.Int64("tourist.id", touristID))

	var body struct {
		TourId int64 `json:"tour_id"`
	}
	if err := c.ShouldBindJSON(&body); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, "Invalid request body")
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request body"})
		return
	}
	span.SetAttributes(attribute.Int64("tour.id", body.TourId))

	resp, err := h.purchaseGrpcClient.RemoveFromCart(ctx, &pb.RemoveFromCartRequest{
		TouristId: touristID,
		TourId:    body.TourId,
	})
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		if st, ok := status.FromError(err); ok {
			c.JSON(http.StatusBadRequest, gin.H{"error": st.Message()})
		} else {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		}
		return
	}

	span.SetStatus(codes.Ok, "Tour removed from cart")
	c.JSON(http.StatusOK, resp)
}

func (h *GatewayHandler) CheckoutGrpc(c *gin.Context) {
	ctx, span := otel.Tracer(tracing.ServiceName).Start(c.Request.Context(), "Checkout")
	defer span.End()

	touristID, ok := getUserID(c)
	if !ok {
		return
	}

	span.SetAttributes(attribute.Int64("tourist.id", touristID))

	resp, err := h.purchaseGrpcClient.Checkout(ctx, &pb.CheckoutRequest{
		TouristId: touristID,
	})

	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		log.Printf("[gRPC ERROR] Checkout: %v", err)
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	span.SetAttributes(attribute.Int("tokens.count", len(resp.Tokens)))
	span.SetStatus(codes.Ok, "Checkout completed successfully")
	c.JSON(http.StatusOK, resp)
}

func getUserID(c *gin.Context) (int64, bool) {
	userIDRaw, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not authenticated"})
		return 0, false
	}
	return int64(userIDRaw.(int)), true
}

func (h *GatewayHandler) GetToursForTouristGrpc(c *gin.Context) {
	touristID, ok := getUserID(c)
	if !ok {
		return
	}

	resp, err := h.purchaseGrpcClient.GetToursForTourist(context.Background(), &pb.GetToursForTouristRequest{
		TouristId: touristID,
	})
	if err != nil {
		log.Printf("[gRPC ERROR] GetToursForTourist: %v", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, resp.Tours)
}

func (h *GatewayHandler) proxyRequest(c *gin.Context, targetURL string) {
	bodyBytes, err := io.ReadAll(c.Request.Body)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to read request body"})
		return
	}
	defer c.Request.Body.Close()

	req, err := http.NewRequest(c.Request.Method, targetURL, bytes.NewReader(bodyBytes))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to build proxy request"})
		return
	}

	for key, values := range c.Request.Header {
		for _, val := range values {
			req.Header.Add(key, val)
		}
	}

	req.URL.RawQuery = c.Request.URL.RawQuery

	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		log.Printf("[Proxy ERROR] %s → %v", targetURL, err)
		c.JSON(http.StatusBadGateway, gin.H{"error": "Service unavailable: " + err.Error()})
		return
	}
	defer resp.Body.Close()

	for key, values := range resp.Header {
		for _, val := range values {
			c.Header(key, val)
		}
	}

	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to read service response"})
		return
	}

	log.Printf("[Proxy] %s → %d", targetURL, resp.StatusCode)
	c.Status(resp.StatusCode)
	c.Writer.Write(respBody)
}
