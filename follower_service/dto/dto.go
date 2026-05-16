package dto

type FollowRequestDTO struct {
	FolloweeID string `json:"followee_mail"`
}

type RecommendationDTO struct {
	UserID string `json:"user_mail"`
	Score  int    `json:"score"`
}
