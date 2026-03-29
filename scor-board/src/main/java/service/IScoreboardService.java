package service;

import model.Match;
import model.Team;

import java.util.List;

public interface IScoreboardService {

    Match startMatch(Team home, Team away);
    List<Match> getSummary();
    void updateScore(Match match, long homeScore, long awayScore);
    void finishMatch(Match match);


}
