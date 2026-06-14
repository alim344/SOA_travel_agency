package main

import (
	"log"
	"os"

	"api-gateway/internal/handler"
	"api-gateway/internal/middleware"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
)

func main() {
	port := os.Getenv("PORT")
	if port == "" {
		port = "8000"
	}

	stakeholdersServiceURL := os.Getenv("STAKEHOLDERS_SERVICE_URL")
	if stakeholdersServiceURL == "" {
		stakeholdersServiceURL = "http://stakeholders-service:8080"
	}

	blogServiceURL := os.Getenv("BLOG_SERVICE_URL")
	if blogServiceURL == "" {
		blogServiceURL = "http://blogs-service:8001"
	}

	tourServiceURL := os.Getenv("TOUR_SERVICE_URL")
	if tourServiceURL == "" {
		tourServiceURL = "http://tour-service:8085"
	}

	followerServiceURL := os.Getenv("FOLLOWER_SERVICE_URL")
	if followerServiceURL == "" {
		followerServiceURL = "http://follower-service:8084"
	}

	tourGrpcAddr := os.Getenv("TOUR_GRPC_ADDR")
	if tourGrpcAddr == "" {
		tourGrpcAddr = "tour-service:9090"
	}

	purchaseGrpcAddr := os.Getenv("PURCHASE_GRPC_ADDR")
	if purchaseGrpcAddr == "" {
		purchaseGrpcAddr = "purchase-service:9093"
	}

	authServiceURL := stakeholdersServiceURL

	router := gin.Default()
	router.SetTrustedProxies(nil)
	router.RedirectTrailingSlash = false

	config := cors.DefaultConfig()
	config.AllowOrigins = []string{"http://localhost:5173"}
	config.AllowMethods = []string{"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"}
	config.AllowHeaders = []string{
		"Origin", "Content-Type", "Accept",
		"Authorization", "X-Requested-With",
		"X-User-ID", "X-User-Role",
	}
	config.AllowCredentials = true
	router.Use(cors.New(config))

	h := handler.NewGatewayHandler(stakeholdersServiceURL, blogServiceURL, tourServiceURL, followerServiceURL, tourGrpcAddr, purchaseGrpcAddr)
	authMiddleware := middleware.NewAuthMiddleware(authServiceURL)

	router.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{"status": "OK", "service": "API Gateway"})
	})

	// STAKEHOLDERS SERVICE (Spring, port 8080)
	//  no token
	auth := router.Group("/auth")
	{
		auth.POST("/register", h.ProxyToStakeholders)
		auth.POST("/login", h.ProxyToStakeholders)
		auth.GET("/userEnabled", h.ProxyToStakeholders)
		auth.GET("/userByEmail/:email", h.ProxyToStakeholders)
	}

	// AdminController — @RequestMapping("/admin")
	// Token required
	admin := router.Group("/admin")
	admin.Use(authMiddleware.ValidateToken())
	{
		admin.GET("/getAccounts", h.ProxyToStakeholders)
		admin.PATCH("/blockUser/:id", h.ProxyToStakeholders)
		admin.PATCH("/unblockUser/:id", h.ProxyToStakeholders)
	}

	// ProfileController — @RequestMapping("/profiles")
	// Token required
	profiles := router.Group("/profiles")
	profiles.Use(authMiddleware.ValidateToken())
	{
		profiles.GET("/me", h.ProxyToStakeholders)
		profiles.PUT("/update", h.ProxyToStakeholders)
	}

	// BLOG SERVICE ( port 8001)
	// Has its own auth
	blog := router.Group("/api/blogs")
	{
		// blogs.router routes
		blog.POST("/", h.ProxyToBlog)
		blog.GET("/", h.ProxyToBlog)
		blog.GET("/all", h.ProxyToBlog)
		blog.GET("/:blog_id", h.ProxyToBlog)
		blog.PUT("/:blog_id", h.ProxyToBlog)
		blog.DELETE("/:blog_id", h.ProxyToBlog)
		blog.POST("/:blog_id/like", h.ProxyToBlog)

		// comments
		blog.POST("/:blog_id/comments", h.ProxyToBlog)
		blog.GET("/:blog_id/comments", h.ProxyToBlog)
		blog.PUT("/:blog_id/comments/:comment_id", h.ProxyToBlog)
		blog.DELETE("/:blog_id/comments/:comment_id", h.ProxyToBlog)
	}

	// FOLLOWER SERVICE ( port 8084)
	// Token required — follower service reads email from JWT itself,
	follower := router.Group("/follower")
	follower.Use(authMiddleware.ValidateToken())
	{
		follower.POST("/follow", h.ProxyToFollower)
		follower.POST("/unfollow", h.ProxyToFollower)
		follower.GET("/followers/:mail", h.ProxyToFollower)
		follower.GET("/followees/:mail", h.ProxyToFollower)
		follower.GET("/following/:mail", h.ProxyToFollower)
		follower.GET("/is-following/:followerId/:followeeId", h.ProxyToFollower)
		follower.GET("/recommendations", h.ProxyToFollower)
	}

	//PURCHASE SERVICE (Spring, port 9093 token required)
	purchase := router.Group("/purchase")
	purchase.Use(authMiddleware.ValidateToken())
	{
		purchase.POST("/add", h.AddToCartGrpc)
		purchase.POST("/remove", h.RemoveFromCartGrpc)
		purchase.POST("/checkout", h.CheckoutGrpc)
		purchase.GET("/cart", h.GetCartGrpc)
		purchase.GET("/my-tours", h.GetToursForTouristGrpc)
	}

	// TOUR SERVICE (Spring, port 8080)
	// Tour routes — token required
	javneTure := router.Group("/tour")
	{
		javneTure.GET("/getAllDtos", h.ProxyToTours)
		javneTure.GET("/getAllActiveTours", h.ProxyToTours)
	}

	tour := router.Group("/tour")
	tour.Use(authMiddleware.ValidateToken())
	{
		tour.GET("/author/:authorId", h.ProxyToTours)
		tour.GET("/nodraft/guide/:authorId", h.ProxyToTours)
		tour.GET("/author", h.ProxyToTours)
		tour.PUT("/:id/publish", h.ProxyToTours)
		tour.POST("/:id/duration", h.ProxyToTours)
		tour.POST("/:id/archive", h.ProxyToTours)
		tour.POST("/:id/reactivate", h.ProxyToTours)
		tour.POST("", h.CreateTourGrpc)     //
		tour.GET("/:id", h.GetTourByIdGrpc) //
		tour.PUT("/:id/price", h.ProxyToTours)
	}

	session := router.Group("/session")
	session.Use(authMiddleware.ValidateToken())
	{
		session.POST("/start", h.ProxyToTours)
		session.POST("/abandon/:executionId", h.ProxyToTours)
		//session.POST("/check-position/:executionId", h.ProxyToTours)
		session.POST("/check-position/:executionId", h.CheckPositionGrpc)
	}

	// KeyPoint routes — token required
	keypoint := router.Group("/keypoint")
	keypoint.Use(authMiddleware.ValidateToken())
	{
		keypoint.GET("/getDtosByTour/:tourId", h.ProxyToTours)
		keypoint.POST("/add/:tourId", h.ProxyToTours)
		keypoint.DELETE("/delete/:id", h.ProxyToTours)
		keypoint.PUT("/update", h.ProxyToTours)
	}

	// Review routes — token required
	review := router.Group("/review")
	review.Use(authMiddleware.ValidateToken())
	{
		review.POST("/:tourId", h.ProxyToTours)
		review.GET("/:tourId", h.ProxyToTours)
	}

	log.Printf("API Gateway starting on port %s", port)
	log.Printf("Stakeholders: %s", stakeholdersServiceURL)
	log.Printf("Blog:         %s", blogServiceURL)
	log.Printf("Tours:        %s", tourServiceURL)
	log.Printf("Follower:     %s", followerServiceURL)
	log.Printf("Purchase:     %s", purchaseGrpcAddr)

	if err := router.Run(":" + port); err != nil {
		log.Fatal("Failed to start API Gateway:", err)
	}
}
