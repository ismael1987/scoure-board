import model.Match;
import model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.IScoreboardService;
import service.impl.ScoreboardServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreboardServiceTest {
    private IScoreboardService scoreboard;

    @BeforeEach
    void setUp() {
        scoreboard = new ScoreboardServiceImpl();
    }

    @Test
    void testStartMatch() {
        Match match = scoreboard.startMatch(Team.MEXICO, Team.CANADA);
        assertNotNull(match);
        assertEquals(Team.MEXICO, match.getHomeTeam());
        assertEquals(Team.CANADA, match.getAwayTeam());

        List<Match> summary = scoreboard.getSummary();
        assertFalse(summary.isEmpty()); // Match should be in summary after starting
    }

    @Test
    void testUpdateScore() {
        Match match = scoreboard.startMatch(Team.SPAIN, Team.BRAZIL);
        scoreboard.updateScore(match, 3, 2);

        assertEquals(3, match.getHomeScore());
        assertEquals(2, match.getAwayScore());

        // Test negative score exception
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                scoreboard.updateScore(match, -1, 2));
        assertEquals("Scores cannot be negative", exception.getMessage());
    }

    @Test
    void testUpdateNonExistentMatch() {
        Match match = new Match(Team.MEXICO, Team.CANADA);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                scoreboard.updateScore(match, 1, 1)
        );

        assertEquals("Match not found on scoreboard", exception.getMessage());
    }

    @Test
    void testGetSummaryOrdering() {
        Match m1 = scoreboard.startMatch(Team.MEXICO, Team.CANADA);
        Match m2 = scoreboard.startMatch(Team.SPAIN, Team.BRAZIL);
        Match m3 = scoreboard.startMatch(Team.GERMANY, Team.FRANCE);
        Match m4 = scoreboard.startMatch(Team.URUGUAY, Team.ITALY);
        Match m5 = scoreboard.startMatch(Team.ARGENTINA, Team.AUSTRALIA);

        scoreboard.updateScore(m1, 0, 5);  // total 5
        scoreboard.updateScore(m2, 10, 2); // total 12
        scoreboard.updateScore(m3, 2, 2);  // total 4
        scoreboard.updateScore(m4, 6, 6);  // total 12
        scoreboard.updateScore(m5, 3, 1);  // total 4

        List<Match> summary = scoreboard.getSummary();

        // Check correct order: total score desc, tie-breaker by recent start
        assertEquals(m4, summary.get(0)); // Uruguay 6-6 Italy
        assertEquals(m2, summary.get(1)); // Spain 10-2 Brazil
        assertEquals(m1, summary.get(2)); // Mexico 0-5 Canada
        assertEquals(m5, summary.get(3)); // Argentina 3-1 Australia
        assertEquals(m3, summary.get(4)); // Germany 2-2 France
    }

    @Test
    void testFinishMatch() {
        Match m1 = scoreboard.startMatch(Team.MEXICO, Team.CANADA);
        Match m2 = scoreboard.startMatch(Team.SPAIN, Team.BRAZIL);

        scoreboard.updateScore(m1, 0, 5);  // total 5
        scoreboard.updateScore(m2, 10, 2); // total 12

        List<Match> summary = scoreboard.getSummary();

        scoreboard.finishMatch(m2); // Finish Spain vs Brazil
        summary = scoreboard.getSummary();
        assertEquals(m1, summary.get(0)); // Mexico 0-5 Canada
    }

    @Test
    void testFinishMatchthatIsNotFound() {
        Match match = new Match(Team.MEXICO, Team.CANADA);

        List<Match> summary = scoreboard.getSummary();
        assertTrue(summary.isEmpty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                scoreboard.finishMatch(match)
        );

        assertEquals("Match not found on scoreboard", exception.getMessage());

    }
}
