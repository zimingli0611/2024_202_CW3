import java.util.*;

public class Rogue {
    private Game game;
    private Dungeon dungeon;
    private int N;

    public Rogue(Game game) {
        this.game    = game;
        this.dungeon = game.getDungeon();
        this.N       = dungeon.size();
    }


    // TAKE A RANDOM LEGAL MOVE
    // YOUR MAIN TASK IS TO RE-IMPLEMENT THIS METHOD TO DO SOMETHING INTELLIGENT
    public Site move() {
        Site monster = game.getMonsterSite();
        Site rogue   = game.getRogueSite();

        int[][] monsterMap = bfs(dungeon.copyMap(), monster);
        int[][] calMap = dungeon.copyMap();
        calMap[monster.i()][monster.j()] = -2;
        int[][] rogueMap = bfs(calMap, rogue);

        //System.out.println("Monster");
        //printMap(monsterMap);
        //System.out.println("Rogue");
        //printMap(rogueMap);

        Site farthestSite = getFarthestSite(monsterMap, rogueMap);
        return getNextMove(rogueMap, monsterMap, farthestSite);
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

    private List<Site> getNeighbors(Site site) {
        List<Site> neighbors = new ArrayList<>();
        int[] directions = {-1, 0, 1};
        for (int di : directions) {
            for (int dj : directions) {
                if (di != 0 || dj != 0) {
                    neighbors.add(new Site(site.i() + di, site.j() + dj));
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

    public Site getFarthestSite(int[][] monsterMap, int[][] rogueMap) {
        Site farthest = null;
        int maxDepth = -1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (monsterMap[i][j] > maxDepth && rogueMap[i][j] > 0) {
                    maxDepth = monsterMap[i][j];
                    farthest = new Site(i, j);
                }
            }
        }
        if(farthest == null) return game.getRogueSite();
        return farthest;
    }

    public Site getNextMove(int[][] rougeMap, int[][] monsterMap, Site target) {
        if(target.equals(game.getRogueSite())) return target;

        Site currentSite = target;
        while (rougeMap[currentSite.i()][currentSite.j()] != 1) {
            Site bestSite = null;
            int monsterDistance = -1;
            for (Site neighbor : getNeighbors(currentSite)) {
                if (dungeon.isLegalMove(currentSite, neighbor) &&
                        rougeMap[neighbor.i()][neighbor.j()] == rougeMap[currentSite.i()][currentSite.j()] - 1 &&
                        monsterMap[neighbor.i()][neighbor.j()] > monsterDistance) {
                    monsterDistance = monsterMap[neighbor.i()][neighbor.j()];
                    bestSite = neighbor;
                }
            }
            currentSite = bestSite;
        }
        return currentSite;
    }
}
