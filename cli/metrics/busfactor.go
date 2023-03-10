package metrics

import (
	log "github.com/sirupsen/logrus"
)

// Bus Factor: 1 - (1 / (# of contributors))
type BusFactorMetric struct {
	numContributors float64
}

func (l BusFactorMetric) CalculateScore(m Module) float64 {
	// Object l of type license matrix and m of type module with function get_url()\
	log.Info("Calculating busFactor metric for module:", m.GetGitHubUrl())
	numContributors := float64(m.GetContributorCount())
	if numContributors == 0 {
		log.Debugf("0 contributers for %v", m.GetGitHubUrl())
		return 0.0
	}
	busFactor := 1.0 - (1.0 / numContributors)
	return busFactor
}
