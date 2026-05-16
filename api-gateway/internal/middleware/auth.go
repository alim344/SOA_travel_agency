package middleware

import (
	"encoding/json"
	"io"
	"log"
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
)

type AuthMiddleware struct {
	authServiceURL string
}

func NewAuthMiddleware(authServiceURL string) *AuthMiddleware {
	return &AuthMiddleware{authServiceURL: authServiceURL}
}

// ValidateToken
func (m *AuthMiddleware) ValidateToken() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")

		if authHeader == "" {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Authorization header required"})
			c.Abort()
			return
		}

		if !strings.HasPrefix(authHeader, "Bearer ") {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Authorization header must start with Bearer"})
			c.Abort()
			return
		}

		token := strings.TrimSpace(strings.TrimPrefix(authHeader, "Bearer "))
		if token == "" {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Token is empty"})
			c.Abort()
			return
		}

		// Call stakeholders /auth/userEnabled to verify the token
		validateURL := m.authServiceURL + "/auth/userEnabled"
		log.Printf("[AuthMiddleware] Validating token at %s", validateURL)

		req, err := http.NewRequest(http.MethodGet, validateURL, nil)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create validation request"})
			c.Abort()
			return
		}
		req.Header.Set("Authorization", "Bearer "+token)

		resp, err := http.DefaultClient.Do(req)
		if err != nil {
			log.Printf("[AuthMiddleware] Could not reach auth service: %v", err)
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Auth service unavailable"})
			c.Abort()
			return
		}
		defer resp.Body.Close()

		bodyBytes, _ := io.ReadAll(resp.Body)

		if resp.StatusCode != http.StatusOK {
			log.Printf("[AuthMiddleware] Token rejected — status %d, body: %s", resp.StatusCode, string(bodyBytes))
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid or expired token"})
			c.Abort()
			return
		}

		var parsed struct {
			ID       int64  `json:"id"`
			Email    string `json:"email"`
			Username string `json:"username"`
			Enabled  bool   `json:"enabled"`
			Role     struct {
				ID   int    `json:"id"`
				Name string `json:"name"`
			} `json:"role"`
		}
		if err := json.Unmarshal(bodyBytes, &parsed); err != nil {
			log.Printf("[AuthMiddleware] Failed to parse validation response: %v", err)
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse auth response"})
			c.Abort()
			return
		}

		if !parsed.Enabled {
			log.Printf("[AuthMiddleware] User %d is disabled", parsed.ID)
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Account is disabled"})
			c.Abort()
			return
		}

		log.Printf("[AuthMiddleware] Token valid — userId=%d, username=%s, role=%s",
			parsed.ID, parsed.Username, parsed.Role.Name)

		// Store in context so handlers can access them
		c.Set("userID", int(parsed.ID))
		c.Set("username", parsed.Username)
		c.Set("userRole", parsed.Role.Name)

		c.Next()
	}
}
