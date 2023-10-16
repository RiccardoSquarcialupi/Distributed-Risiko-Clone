package app.game.GUI;

import app.manager.gui.GUI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class GUIGame extends JPanel implements GUI, GUIGameActions {
    @Override
    public String getTitle() {
        return "RiSiKo!!!";
    }

    public GUIGame() {
        ImageIcon mapImage = new ImageIcon("./assets/image/map.png");
        JLabel gameBoard = new JLabel(mapImage);
        add(gameBoard);
        Map<String, List<PairOfCoordinates>> boardMap = parseJsonMap();
        var mapBorder = addClickableCountries(boardMap);
        mapBorder.forEach((panel) -> {
            panel.addMouseListener(onCountryClick());
            add(panel);
        });



    }

    private MouseListener onCountryClick() {
        return new MouseListener() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.out.println("Clicked on " + ((JPanel) e.getSource()).getName());
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // TODO Auto-generated method stub

            }
        };
    }

    private List<JPanel> addClickableCountries(Map<String, List<PairOfCoordinates>> boardMap) {
        List<JPanel> mapBorder = new ArrayList<>();
        boardMap.forEach((country, coords) -> {
            Polygon p = new Polygon();
            coords.forEach((pair) -> {
                p.addPoint(pair.getX(), pair.getY());
            });
            JPanel pan = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.BLACK);
                    g.drawPolygon(p);
                }

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(1200, 614);
                }
            };
            pan.setName(country);
            mapBorder.add(pan);
        });
        return mapBorder;
    }

    private Map<String, List<PairOfCoordinates>> parseJsonMap() {
        String path = Paths.get("src/main/java/assets/json/RiskMap.json").toAbsolutePath().toString();

        Map<String, List<Integer>> boardMap = new LinkedHashMap<>();
        try {
            // Create an ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            // Read JSON from the file into a JsonNode
            JsonNode rootNode = objectMapper.readTree(new File(path));
            // Access and work with the JSON data
            rootNode.get("map").forEach((JsonNode node) -> {
                String country = node.get("country").asText();
                List<Integer> arr = Arrays.stream(node.get("coords").asText().split(",")).map(Integer::parseInt).collect(Collectors.toList());
                boardMap.put(country, arr);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, List<PairOfCoordinates>> finalMap = new LinkedHashMap<>();
        boardMap.forEach((country, coords) -> {
            var l = new ArrayList<PairOfCoordinates>();
            for (int i = 0; i < coords.size() - 1; i = i + 2) {
                PairOfCoordinates p1 = new PairOfCoordinates(coords.get(i), coords.get(i + 1));
                l.add(p1);
            }
            finalMap.put(country, l);
        });
        return finalMap;


    }

    private class PairOfCoordinates {
        private final int x;
        private final int y;

        public PairOfCoordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }
    }
}
