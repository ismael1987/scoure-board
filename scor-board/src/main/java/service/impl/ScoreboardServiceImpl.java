package service.impl;

import model.Match;
import model.Team;
import service.IScoreboardService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardServiceImpl implements IScoreboardService {

    private final List<Match> matches = new ArrayList<>();
    private static final String MATCH_NOT_FOUND = "Match not found on scoreboard";
    private static final String MATCH_NOT_ABS = "Scores cannot be negative";

    @Override
    public Match startMatch(Team home, Team away) {
        Match match = new Match(home,away);
        matches.add(match);
        return match;
    }

    @Override
    public List<Match> getSummary() {
        return matches.stream().sorted(Comparator
                .comparingLong(Match::getTotalScore)
                .thenComparing(Match::getStartTime).reversed()
        )
                .collect(Collectors.toList());
    }

    @Override
    public void updateScore(Match match, long homeScore, long awayScore) throws IllegalArgumentException {
        validateMatchExists(match);
        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException(MATCH_NOT_ABS);
        }
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
    }

    private void validateMatchExists(Match match) {
        if (!matches.contains(match)) {
            throw new IllegalArgumentException(MATCH_NOT_FOUND);
        }
    }
}
