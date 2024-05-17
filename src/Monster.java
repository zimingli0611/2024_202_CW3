import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Monster {
    private Game game;
    private Dungeon dungeon;
    private int N;

    public Monster(Game game) {
        this.game    = game;
        this.dungeon = game.getDungeon();
        this.N       = dungeon.size();
    }


    // TAKE A RANDOM LEGAL MOVE
    // YOUR TASK IS TO RE-IMPLEMENT THIS METHOD TO DO SOMETHING INTELLIGENT
    public Site move() {
        Site monster = game.getMonsterSite();
        Site rogue   = game.getRogueSite();
        int[][] map = dungeon.copyMap();

        if (dungeon.isLegalMove(monster,rogue))
            return rogue;

        int[][] mapBFS = bfs(map, monster);
        return getNextMove(mapBFS, rogue);
    }

    public List<Site> getNeighbors(Site site) {
        List<Site> neighbors = new ArrayList<>();
        int[] directions = {-1, 0, 1};
        for (int di : directions) {
            for (int dj : directions) {
                if (di != 0 || dj != 0) {
                    int ni = site.i() + di;
                    int nj = site.j() + dj;
                    if (ni >= 0 && ni < N && nj >= 0 && nj < N) {
                        neighbors.add(new Site(ni, nj));
                    }
                }
            }
        }
        return neighbors;
    }

    public int[][] bfs(int[][] map, Site start) {
        Queue<Site> queue = new LinkedList<>();
        queue.add(start);
        map[start.i()][start.j()] = 0;
        int depth = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            depth++;
            for (int i = 0; i < size; i++) {
                Site current = queue.poll();
                for (Site neighbor : getNeighbors(current)) {
                    if (dungeon.isLegalMove(current, neighbor) && map[neighbor.i()][neighbor.j()] == -1) {
                        map[neighbor.i()][neighbor.j()] = depth;
                        queue.add(neighbor);
                    }
                }
            }
        }
        return map;
    }

    public Site getNextMove(int[][] map, Site target) {
        Site rouge = game.getRogueSite();
        Site currentSite = target;
        if (map[target.i()][target.j()] == -1) return game.getMonsterSite();
        while (map[currentSite.i()][currentSite.j()] != 1) {
            Site bestSite = null;
            int rogueDistance = N * N;
            for (Site neighbor : getNeighbors(currentSite)) {
                if (dungeon.isLegalMove(currentSite, neighbor) &&
                        map[neighbor.i()][neighbor.j()] == map[currentSite.i()][currentSite.j()] - 1 &&
                        neighbor.manhattanTo(rouge) < rogueDistance) {
                    bestSite = neighbor;
                    rogueDistance = neighbor.manhattanTo(rouge);
                }
            }
            currentSite = bestSite;
        }
        return currentSite;
    }

    public void printMap(int[][] map) {
        for (int  i = 0; i < N; i++) {
            for (int  j = 0; j < N; j++) {
                System.out.print(map[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}
