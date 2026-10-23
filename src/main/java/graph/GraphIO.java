package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;

public final class GraphIO {
    private GraphIO() {}

    public static Graph read(Path file) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(file)) {
            StringTokenizer head = new StringTokenizer(br.readLine());
            int n = Integer.parseInt(head.nextToken());
            int m = Integer.parseInt(head.nextToken());
            String kind = head.nextToken();
            if (!kind.equals("directed") && !kind.equals("undirected"))
                throw new IOException("expected directed|undirected, got " + kind);
            Graph g = new Graph(n, kind.equals("directed"));
            for (int i = 0; i < m; i++) {
                StringTokenizer st = new StringTokenizer(br.readLine());
                g.addEdge(Integer.parseInt(st.nextToken()),
                          Integer.parseInt(st.nextToken()),
                          Integer.parseInt(st.nextToken()));
            }
            return g;
        }
    }
}
