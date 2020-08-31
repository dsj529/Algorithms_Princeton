import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private final int SOURCE = 0;
    private final int SINK = 1;

    private final int n;
    private final HashMap<String, Integer> idTeamMap;
    private final int[] wins;
    private final int[] losses;
    private final int[] left;
    private final int[][] gamesAgainst;
    private final HashMap<String, HashSet<String>> eliminationMap;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        n = in.readInt();
        idTeamMap = new HashMap<>();
        wins = new int[n];
        losses = new int[n];
        left = new int[n];
        gamesAgainst = new int[n][n];
        eliminationMap = new HashMap<>();
//        int mostWins = 0;

        for (int i = 0; i < n; i++) {
            String team = in.readString();
            idTeamMap.put(team, i);
            wins[i] = in.readInt();
//            if (wins[i] > mostWins) {
//                leader = team;
//                mostWins = wins[i];
//            }
            losses[i] = in.readInt();
            left[i] = in.readInt();
            for (int j = 0; j < n; j++) {
                gamesAgainst[i][j] = in.readInt();
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return n;
    }

    // all teams
    public Iterable<String> teams() {
        return idTeamMap.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        if (!idTeamMap.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return wins[idTeamMap.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (!idTeamMap.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return losses[idTeamMap.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!idTeamMap.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return left[idTeamMap.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!idTeamMap.containsKey(team1) || !idTeamMap.containsKey(team2)) {
            throw new IllegalArgumentException();
        }
        return gamesAgainst[idTeamMap.get(team1)][idTeamMap.get(team2)];

    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!idTeamMap.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return !eliminationSet(team).isEmpty();
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!idTeamMap.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return isEliminated(team) ? eliminationSet(team) : null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

    /***
     * Helper functions down here
     */

    private HashSet<String> eliminationSet(String team) {
        if (!eliminationMap.containsKey(team)) {
            if (!trivialElim(team)) {
                calcElimination(team);
            }
        }
        return eliminationMap.get(team);
    }

    private boolean trivialElim(String team) {
        int id = idTeamMap.get(team);
        int maxW = wins[id] + left[id];

        HashSet<String> elimTeams = new HashSet<>();
        for (String t : idTeamMap.keySet()) {
            int i = idTeamMap.get(t);
            if (i != id) {
                if (maxW < wins[i]) {
                    elimTeams.add(t);
                }
            }
        }
        eliminationMap.put(team, elimTeams);
        return !elimTeams.isEmpty();
    }

    private void calcElimination(String team) {
        int tIdx = idTeamMap.get(team);
        int teams = n;
        int opps = n-1;
        int gamesToPlay = teams * opps / 2;
        int oppGames = gamesToPlay - opps;

        int edgeNum = 2 + oppGames + opps;

        FlowNetwork network = new FlowNetwork(edgeNum);
        int gameEdgeIdx = opps + 2;
        int maxWins = wins[tIdx] + left[tIdx];

        for (int i = 0; i < n; i++) {
            if (i == tIdx) {
                continue;
            }
            int teamAIdx = i < tIdx ? i + 2 : i + 1;
            FlowEdge teamToSinkEdge = new FlowEdge(teamAIdx, SINK, maxWins - wins[i]);
            network.addEdge(teamToSinkEdge);
        
            for (int j = 0; j<i; j++) {
                if (j==tIdx) {continue;}
                
                int gCount = gamesAgainst[i][j];
                FlowEdge sourceToGameEdge = new FlowEdge(SOURCE, gameEdgeIdx, gCount);
                network.addEdge(sourceToGameEdge);
                
                FlowEdge teamAWinEdge = new FlowEdge(gameEdgeIdx, teamAIdx, Double.POSITIVE_INFINITY); 
                network.addEdge(teamAWinEdge);
                
                int teamBIdx = j < tIdx? j+2:j+1;
                FlowEdge teamBWinEdge = new FlowEdge(gameEdgeIdx, teamBIdx, Double.POSITIVE_INFINITY); 
                network.addEdge(teamBWinEdge);
                
                gameEdgeIdx++;
            }
        }
        FordFulkerson maxFlow = new FordFulkerson(network, SOURCE, SINK);
        HashSet<String> elimTeams = new HashSet<>();
        
        for (String t:idTeamMap.keySet()) {
            int i = idTeamMap.get(t);
            if (i == tIdx) {continue;}
            
            int oppIdx = i<tIdx? i+2:i+1;
            if (maxFlow.inCut(oppIdx)) {
                elimTeams.add(t);
            }
        }
        eliminationMap.put(team, elimTeams);
    }
}
