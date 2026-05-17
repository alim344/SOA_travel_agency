package main

import (
	"encoding/json"
	"fmt"
	"follower_service/dto"
	"follower_service/repository"
	"log"
	"net/http"
	"os"
	"strings"

	"github.com/golang-jwt/jwt/v4"
	"github.com/gorilla/mux"
)

func main() {
	logger := log.New(os.Stdout, "follower_service: ", log.LstdFlags)
	repo, err := repository.New(logger)
	if err != nil {
		logger.Fatalf("Failed to initialize repository: %v", err)
	}

	stakeholdersURL := os.Getenv("STAKEHOLDERS_URL")
	if stakeholdersURL == "" {
		stakeholdersURL = "http://localhost:8080"
	}

	router := mux.NewRouter()

	router.HandleFunc("/follow", func(w http.ResponseWriter, r *http.Request) {
		followerID, err := getEmailFromToken(r)
		if err != nil {
			http.Error(w, "Unauthorized", http.StatusUnauthorized)
			return
		}

		var req dto.FollowRequestDTO
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			http.Error(w, "Invalid request body", http.StatusBadRequest)
			return
		}

		if req.FolloweeID == "" {
			http.Error(w, "Missing followee_mail", http.StatusBadRequest)
			return
		}

		if followerID == req.FolloweeID {
			http.Error(w, "You cannot follow yourself", http.StatusBadRequest)
			return
		}

		if !userExists(stakeholdersURL, req.FolloweeID) {
			http.Error(w, "User does not exist", http.StatusNotFound)
			return
		}

		isFollowing, err := repo.Is_Following(followerID, req.FolloweeID)
		if err != nil {
			http.Error(w, "Failed to check following status", http.StatusInternalServerError)
			return
		}

		if isFollowing {
			//unfollow
			err = repo.DeleteFollow(followerID, req.FolloweeID)
			if err != nil {
				http.Error(w, "Failed to unfollow", http.StatusInternalServerError)
				return
			}
			w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(map[string]any{"following": false, "message": "Unfollowed"})
			return
		}

		err = repo.CreateFollow(followerID, req.FolloweeID)
		if err != nil {
			logger.Printf("Failed to create follow relationship: %v", err)
			http.Error(w, "Failed to create follow relationship", http.StatusInternalServerError)
			return
		}
		w.WriteHeader(http.StatusCreated)
	}).Methods("POST")

	router.HandleFunc("/unfollow", func(w http.ResponseWriter, r *http.Request) {
		followerID, err := getEmailFromToken(r)
		if err != nil {
			http.Error(w, "Unauthorized", http.StatusUnauthorized)
			return
		}

		var req dto.FollowRequestDTO
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			http.Error(w, "Invalid request body", http.StatusBadRequest)
			return
		}

		if req.FolloweeID == "" {
			http.Error(w, "Missing followee_mail", http.StatusBadRequest)
			return
		}

		err = repo.DeleteFollow(followerID, req.FolloweeID)
		if err != nil {
			logger.Printf("Failed to delete follow relationship: %v", err)
			http.Error(w, "Failed to delete follow relationship", http.StatusInternalServerError)
			return
		}
		w.WriteHeader(http.StatusOK)
	}).Methods("POST")

	router.HandleFunc("/followers/{mail}", func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		userID := vars["mail"]
		if userID == "" {
			http.Error(w, "Invalid user ID", http.StatusBadRequest)
			return
		}
		followers, err := repo.GetFollowers(userID)
		if err != nil {
			logger.Printf("Failed to get followers: %v", err)
			http.Error(w, "Failed to get followers", http.StatusInternalServerError)
			return
		}
		if followers == nil {
			followers = []string{}
		}
		json.NewEncoder(w).Encode(followers)
	}).Methods("GET")

	router.HandleFunc("/followees/{mail}", func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		userID := vars["mail"]
		if userID == "" {
			http.Error(w, "Invalid user ID", http.StatusBadRequest)
			return
		}
		followees, err := repo.GetFollowees(userID)
		if err != nil {
			logger.Printf("Failed to get followees: %v", err)
			http.Error(w, "Failed to get followees", http.StatusInternalServerError)
			return
		}
		if followees == nil {
			followees = []string{}
		}
		json.NewEncoder(w).Encode(followees)
	}).Methods("GET")

	router.HandleFunc("/following/{mail}", func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		userID := vars["mail"]
		if userID == "" {
			http.Error(w, "Invalid user ID", http.StatusBadRequest)
			return
		}
		followees, err := repo.GetFollowees(userID)
		if err != nil {
			logger.Printf("Failed to get followees: %v", err)
			http.Error(w, "Failed to get followees", http.StatusInternalServerError)
			return
		}
		if followees == nil {
			followees = []string{}
		}
		json.NewEncoder(w).Encode(followees)
	}).Methods("GET")

	router.HandleFunc("/is-following/{followerId}/{followeeId}", func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		followerID := vars["followerId"]
		if followerID == "" {
			http.Error(w, "Invalid follower ID", http.StatusBadRequest)
			return
		}
		followeeID := vars["followeeId"]
		if followeeID == "" {
			http.Error(w, "Invalid followee ID", http.StatusBadRequest)
			return
		}
		isFollowing, err := repo.Is_Following(followerID, followeeID)
		if err != nil {
			logger.Printf("Failed to check following status: %v", err)
			http.Error(w, "Failed to check following status", http.StatusInternalServerError)
			return
		}
		json.NewEncoder(w).Encode(isFollowing)
	}).Methods("GET")

	router.HandleFunc("/recommendations", func(w http.ResponseWriter, r *http.Request) {
		userID, err := getEmailFromToken(r)
		if err != nil {
			http.Error(w, "Unauthorized", http.StatusUnauthorized)
			return
		}
		recs, err := repo.GetRecommendations(userID)
		if err != nil {
			logger.Printf("Failed to get recommendations: %v", err)
			http.Error(w, "Failed to get recommendations", http.StatusInternalServerError)
			return
		}
		if recs == nil {
			recs = []dto.RecommendationDTO{}
		}
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(recs)
	}).Methods("GET")

	logger.Println("Follower service is running on port 8084")
	logger.Fatal(http.ListenAndServe(":8084", router))
}

func getEmailFromToken(r *http.Request) (string, error) {
	authHeader := r.Header.Get("Authorization")
	if authHeader == "" || !strings.HasPrefix(authHeader, "Bearer ") {
		return "", fmt.Errorf("missing token")
	}
	tokenStr := strings.TrimPrefix(authHeader, "Bearer ")

	token, _, err := new(jwt.Parser).ParseUnverified(tokenStr, jwt.MapClaims{})
	if err != nil {
		return "", err
	}
	claims, ok := token.Claims.(jwt.MapClaims)
	if !ok {
		return "", fmt.Errorf("invalid claims")
	}
	email, ok := claims["sub"].(string)
	if !ok {
		return "", fmt.Errorf("no sub claim")
	}
	return email, nil
}

func userExists(stakeholdersURL, email string) bool {
	resp, err := http.Get(stakeholdersURL + "/auth/userByEmail/" + email)
	if err != nil {
		return false
	}
	defer resp.Body.Close()
	return resp.StatusCode == 200
}
