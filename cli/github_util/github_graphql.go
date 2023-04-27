package github_util

import (
	"context"
	log "github.com/sirupsen/logrus"
	"os"

	"github.com/Alethon/ECE461_Part2/cli/metrics"
	"github.com/hasura/go-graphql-client"
	"golang.org/x/oauth2"
)

func GetLast10ClosedIssues(owner string, name string) []metrics.IssueNode {
	src := oauth2.StaticTokenSource(
		&oauth2.Token{AccessToken: os.Getenv("API_KEY")},
	)
	httpClient := oauth2.NewClient(context.Background(), src)

	client := graphql.NewClient("https://api.github.com/graphql", httpClient)

	variables := map[string]interface{}{
		"owner": graphql.String(owner),
		"name":  graphql.String(name),
	}
	var query struct {
		Repository struct {
			Issues struct {
				Edges []struct {
					Node metrics.IssueNode
				}
			} `graphql:"issues(last:10, states:CLOSED)"`
		} `graphql:"repository(owner: $owner, name: $name)"`
	}

	err := client.Query(context.Background(), &query, variables)
	if err != nil {
		log.Debug(err)
	}

	IssueNodes := []metrics.IssueNode{}
	for _, issue := range query.Repository.Issues.Edges {
		IssueNodes = append(IssueNodes, issue.Node)
	}

	return IssueNodes
}
