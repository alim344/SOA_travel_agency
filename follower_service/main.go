package main

import (
	"encoding/json"
	"follower_service/dto"
	"follower_service/repository"
	"log"
	"net/http"
	"os"

	"github.com/gorilla/mux"
)

func main() {
	logger := log.New(os.Stdout, "follower_service: ", log.LstdFlags)
	repo, err := repository.New(logger)
	if err != nil {
		logger.Fatalf("Failed to initialize repository: %v", err)
	}

	router := mux.NewRouter()
	router.HandleFunc("/follow", func(w http.ResponseWriter, r *http.Request) {
		var req dto.FollowRequestDTO
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			http.Error(w, "Invalid request body", http.StatusBadRequest)
			return
		}

		err := repo.CreateFollow(req.FollowerID, req.FolloweeID)
		if err != nil {
			logger.Printf("Failed to create follow relationship: %v", err)
			http.Error(w, "Failed to create follow relationship", http.StatusInternalServerError)
			return
		}
		w.WriteHeader(http.StatusCreated)
	}).Methods("POST")

	router.HandleFunc("/unfollow", func(w http.ResponseWriter, r *http.Request) {
		var req dto.FollowRequestDTO
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			http.Error(w, "Invalid request body", http.StatusBadRequest)
			return
		}

		err := repo.DeleteFollow(req.FollowerID, req.FolloweeID)
		if err != nil {
			logger.Printf("Failed to delete follow relationship: %v", err)
			http.Error(w, "Failed to delete follow relationship", http.StatusInternalServerError)
			return
		}
		w.WriteHeader(http.StatusCreated)
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

	router.HandleFunc("/recommendations/{mail}", func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		userID := vars["mail"]
		if userID == "" {
			http.Error(w, "Invalid user ID", http.StatusBadRequest)
			return
		}

		recs, err := repo.GetRecommendations(userID)
		if err != nil {
			logger.Printf("Failed to get recommendations: %v", err)
			http.Error(w, "Failed to get recommendations", http.StatusInternalServerError)
			return
		}
		json.NewEncoder(w).Encode(recs)
	}).Methods("GET")

	logger.Println("Follower service is running on port 8084")
	logger.Fatal(http.ListenAndServe(":8084", router))
}
