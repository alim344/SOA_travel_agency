package repository

import (
	"context"
	"follower_service/dto"
	"log"
	"os"

	"github.com/google/uuid"

	"github.com/neo4j/neo4j-go-driver/v5/neo4j"
)

type FollowerRepository struct {
	driver neo4j.DriverWithContext
	Logger *log.Logger
}

func New(logger *log.Logger) (*FollowerRepository, error) {
	uri := os.Getenv("NEO4J_URI")
	if uri == "" {
		uri = "bolt://localhost:7687"
	}
	username := os.Getenv("NEO4J_USERNAME")
	if username == "" {
		username = "neo4j"
	}
	password := os.Getenv("NEO4J_PASSWORD")
	if password == "" {
		password = "password"
	}

	auth := neo4j.BasicAuth(username, password, "")
	driver, err := neo4j.NewDriverWithContext(uri, auth)

	if err != nil {
		return nil, err
	}

	return &FollowerRepository{
		driver: driver,
		Logger: logger,
	}, nil
}

func (repo *FollowerRepository) CreateFollow(followerID, followeeID uuid.UUID) error {
	ctx := context.Background()
	session := repo.driver.NewSession(ctx, neo4j.SessionConfig{DatabaseName: "neo4j"})
	defer session.Close(ctx)

	_, err := session.ExecuteWrite(ctx, func(tx neo4j.ManagedTransaction) (any, error) {
		query := `
			merge (follower:User {id: $followerID})
			merge (followee:User {id: $followeeID})
			merge (follower)-[:FOLLOWS]->(followee)
			return follower
		`
		params := map[string]any{
			"followerID": followerID.String(),
			"followeeID": followeeID.String()}
		res, err := tx.Run(ctx, query, params)
		if err != nil {
			return nil, err
		}
		return nil, res.Err()
	})
	return err
}

func (repo *FollowerRepository) DeleteFollow(followerID, followeeID uuid.UUID) error {
	ctx := context.Background()
	session := repo.driver.NewSession(ctx, neo4j.SessionConfig{DatabaseName: "neo4j"})
	defer session.Close(ctx)

	_, err := session.ExecuteWrite(ctx, func(tx neo4j.ManagedTransaction) (any, error) {
		query := `
			match (follower:User {id: $followerID})-[r:FOLLOWS]->(followee:User {id: $followeeID})
			delete r
			return follower
		`
		params := map[string]any{
			"followerID": followerID.String(),
			"followeeID": followeeID.String()}
		res, err := tx.Run(ctx, query, params)

		if err != nil {
			return nil, err
		}
		return nil, res.Err()
	})
	return err
}

func (repo *FollowerRepository) GetFollowers(userID uuid.UUID) ([]uuid.UUID, error) {
	ctx := context.Background()
	session := repo.driver.NewSession(ctx, neo4j.SessionConfig{DatabaseName: "neo4j"})
	defer session.Close(ctx)

	result, err := session.ExecuteRead(ctx, func(tx neo4j.ManagedTransaction) (any, error) {
		query := `
			match (follower:User)-[:FOLLOWS]->(followee:User {id: $userID})
			return follower.id as id
		`
		params := map[string]any{"userID": userID.String()}
		res, err := tx.Run(ctx, query, params)
		if err != nil {
			return nil, err
		}
		var followers []uuid.UUID
		for res.Next(ctx) {
			idStr, _ := res.Record().Values[0].(string)
			id, _ := uuid.Parse(idStr)
			followers = append(followers, id)
		}
		return followers, nil
	})
	if err != nil {
		return nil, err
	}
	return result.([]uuid.UUID), nil
}

func (repo *FollowerRepository) GetFollowees(userID uuid.UUID) ([]uuid.UUID, error) {
	ctx := context.Background()
	session := repo.driver.NewSession(ctx, neo4j.SessionConfig{DatabaseName: "neo4j"})
	defer session.Close(ctx)

	result, err := session.ExecuteRead(ctx, func(tx neo4j.ManagedTransaction) (any, error) {
		query := `
			match (follower:User {id: $userID})-[:FOLLOWS]->(followee:User)
			return followee.id as id
		`
		params := map[string]any{"userID": userID.String()}
		res, err := tx.Run(ctx, query, params)
		if err != nil {
			return nil, err
		}
		var followees []uuid.UUID
		for res.Next(ctx) {
			idStr, _ := res.Record().Values[0].(string)
			id, _ := uuid.Parse(idStr)
			followees = append(followees, id)
		}
		return followees, nil
	})
	if err != nil {
		return nil, err
	}
	return result.([]uuid.UUID), nil
}

func (repo *FollowerRepository) Is_Following(followerID, followeeID uuid.UUID) (bool, error) {
	ctx := context.Background()
	session := repo.driver.NewSession(ctx, neo4j.SessionConfig{DatabaseName: "neo4j"})
	defer session.Close(ctx)

	result, err := session.ExecuteRead(ctx, func(tx neo4j.ManagedTransaction) (any, error) {
		query := `
			match (follower:User {id: $followerID})-[:FOLLOWS]->(followee:User {id: $followeeID})
			return count(*) > 0 as isFollowing
		`
		params := map[string]any{
			"followerID": followerID.String(),
			"followeeID": followeeID.String(),
		}
		res, err := tx.Run(ctx, query, params)
		if err != nil {
			return nil, err
		}
		if res.Next(ctx) {
			return res.Record().Values[0].(bool), nil
		}
		return false, res.Err()
	})
	if err != nil {
		return false, err
	}
	return result.(bool), nil
}

func (repo *FollowerRepository) GetRecommendations(userID uuid.UUID) ([]dto.RecommendationDTO, error) {
	ctx := context.Background()
	session := repo.driver.NewSession(ctx, neo4j.SessionConfig{DatabaseName: "neo4j"})
	defer session.Close(ctx)

	result, err := session.ExecuteRead(ctx, func(tx neo4j.ManagedTransaction) (any, error) {
		query := `
			match (user:User {id: $userID})-[:FOLLOWS]->(followee:User)-[:FOLLOWS]->(recommendation:User)
			where not (user)-[:FOLLOWS]->(recommendation) and user.id <> recommendation.id
			return recommendation.id as id, count(*) as score
			order by score desc
			limit 10
		`
		res, err := tx.Run(ctx, query, map[string]any{"userID": userID.String()})
		if err != nil {
			return nil, err

		}
		var recommendations []dto.RecommendationDTO
		for res.Next(ctx) {
			rec := res.Record()
			idStr, _ := uuid.Parse(rec.Values[0].(string))
			score := int(rec.Values[1].(int64))
			recommendations = append(recommendations, dto.RecommendationDTO{UserID: idStr, Score: score})
		}
		return recommendations, nil
	})
	if err != nil {
		return nil, err
	}
	return result.([]dto.RecommendationDTO), nil
}
