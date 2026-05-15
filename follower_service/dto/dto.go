package dto

import "github.com/google/uuid"

type FollowRequestDTO struct {
	FollowerID uuid.UUID `json:"follower_id"`
	FolloweeID uuid.UUID `json:"followee_id"`
}

type RecommendationDTO struct {
	UserID uuid.UUID `json:"user_id"`
	Score  int       `json:"score"`
}
