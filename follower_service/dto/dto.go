package dto

type FollowRequestDTO struct {
	FollowerID string `json:"follower_mail"`
	FolloweeID string `json:"followee_mail"`
}

type RecommendationDTO struct {
	UserID string `json:"user_mail"`
	Score  int    `json:"score"`
}
