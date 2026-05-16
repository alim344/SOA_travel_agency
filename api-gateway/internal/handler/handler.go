package handler

import (
	"bytes"
	"fmt"
	"io"
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
)

type GatewayHandler struct {
	stakeholdersServiceURL string
	blogServiceURL         string
	tourServiceURL         string
	followerServiceURL     string
}

func NewGatewayHandler(stakeholdersServiceURL, blogServiceURL, tourServiceURL, followerServiceURL string) *GatewayHandler {
	return &GatewayHandler{
		stakeholdersServiceURL: stakeholdersServiceURL,
		blogServiceURL:         blogServiceURL,
		tourServiceURL:         tourServiceURL,
		followerServiceURL:     followerServiceURL,
	}
}

// ─── STAKEHOLDERS ─────────────────────────────────────────────────────────────
func (h *GatewayHandler) ProxyToStakeholders(c *gin.Context) {
	targetURL := h.stakeholdersServiceURL + c.Request.URL.Path
	log.Printf("[Stakeholders] %s %s", c.Request.Method, targetURL)
	h.proxyRequest(c, targetURL)
}

// ─── BLOG ─────────────────────────────────────────────────────────────────────
func (h *GatewayHandler) ProxyToBlog(c *gin.Context) {
	targetURL := h.blogServiceURL + c.Request.URL.Path
	log.Printf("[Blog] %s %s", c.Request.Method, targetURL)
	h.proxyRequest(c, targetURL)
}

// ─── FOLLOWER ─────────────────────────────────────────────────────────────────
func (h *GatewayHandler) ProxyToFollower(c *gin.Context) {
	targetURL := h.followerServiceURL + c.Request.URL.Path
	log.Printf("[Follower] %s %s", c.Request.Method, targetURL)
	h.proxyRequest(c, targetURL)
}

// ─── TOURS ────────────────────────────────────────────────────────────────────
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

// ─── CORE PROXY ───────────────────────────────────────────────────────────────
// Reads the incoming request, forwards it to targetURL,
// and copies the response (status, headers, body) back to the client.
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

	// Forward all headers (Authorization, Content-Type, etc.)
	for key, values := range c.Request.Header {
		for _, val := range values {
			req.Header.Add(key, val)
		}
	}

	// Forward query parameters
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
