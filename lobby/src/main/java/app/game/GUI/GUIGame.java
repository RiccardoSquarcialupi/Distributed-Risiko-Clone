package app.game.GUI;

import app.Launcher;
import app.common.Pair;
import app.game.GameClient;
import app.game.GameClientImpl;
import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import app.lobby.LobbyClientImpl;
import app.lobbySelector.JSONClient;
import app.manager.gui.GUI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class GUIGame extends JPanel implements GUI, GUIGameActions {

    public enum GAME_STATE {WAITING, PLACING, ATTACKING}
    protected final AtomicReference<GAME_STATE> state;
    private final GUIGame guiGame;
    private final List<Color> colors;
    private final JLabel map;

    private void disableActions() {
        SwingUtilities.invokeLater(() -> {
            guiGame.setEnabled(false);
            guiGame.repaint();
            guiGame.revalidate();
        });
    }

    private void enableActions() {
        SwingUtilities.invokeLater(() -> {
            guiGame.setEnabled(true);
            guiGame.repaint();
            guiGame.revalidate();
        });
    }

    @Override
    public String getTitle() {
        return "RiSiKo!!!";
    }

    private JLabel jlState;

    public GUIGame() {
        setLayout(new BorderLayout());
        this.colors = List.of(
            Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN, Color.PINK, Color.GRAY, Color.BLUE, Color.WHITE
        );
        this.jlState = new JLabel("WAITING");
        setTopPanel();
        map = new JLabel();
        map.setIcon(new ImageIcon(Paths.get("src/main/java/assets/image/map.png").toAbsolutePath().toString()));
        add(map, BorderLayout.CENTER);
        map.addMouseListener(onMapClick());
        this.state = new AtomicReference<>(GAME_STATE.WAITING);
        this.guiGame = this;

        this.disableActions();

        /*SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(2000);
                ((GameClientImpl) Launcher.getCurrentClient()).broadcastTerritories();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });*/
    }

    private void setTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(new JLabel("Player: "+ ((GameClientImpl)Launcher.getCurrentClient()).getNickname()));
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(new JLabel("Goal: "+ ((GameClientImpl)Launcher.getCurrentClient()).getGoal()));
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(this.jlState);
        topPanel.add(Box.createHorizontalGlue());
        final String[] enemies = {""};
        ((GameClientImpl)Launcher.getCurrentClient()).getClientList().forEach((JSONClient client) -> {
            if(!client.getNickname().equals(((GameClientImpl)Launcher.getCurrentClient()).getNickname())){
                enemies[0] += client.getNickname() + " ";
            }
        });
        topPanel.add(new JLabel("Enemies: " + enemies[0]));
        topPanel.add(Box.createHorizontalGlue());
        add(topPanel, BorderLayout.NORTH);
    }

    private MouseListener onMapClick() {
        return new MouseListener() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                parseJsonMap().forEach((country, coords) -> {
                    Polygon p = new Polygon();
                    coords.forEach((pair) -> {
                        p.addPoint(pair.getX(), pair.getY());
                    });
                    if (p.contains(e.getPoint())) {
                        System.out.println("Clicked on " + country);
                        switch (state.get()) {
                            case WAITING:
                                break;
                            case PLACING:
                                if(SwingUtilities.isLeftMouseButton(e)){
                                    placeArmy(country, 1);
                                } else if(SwingUtilities.isRightMouseButton(e)){
                                    placeArmy(country, -1);
                                }
                                updateMapImage();
                                break;
                            case ATTACKING:
                                break;
                            default:
                                break;
                        }
                    }
                });
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

    private void placeArmy(String country, Integer deltaArmies){
        ((GameClient)Launcher.getCurrentClient()).placeArmy(country, deltaArmies);
        var ps = ((GameClient) Launcher.getCurrentClient()).getPlacingState();
        if(this.state.get() == GAME_STATE.PLACING){
            this.jlState.setText("Placing armies: " + (ps.getFirst()) + " :/: " + ps.getSecond());
            System.out.println("Placing armies: " + (ps.getFirst()) + " :/: " + ps.getSecond());
        }
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
                //WINDOW is 1600x1000 +180 is magic number for fixing the map because is centered in the JFRAME
                PairOfCoordinates p1 = new PairOfCoordinates(coords.get(i), coords.get(i + 1)+180);
                l.add(p1);
            }
            finalMap.put(country, l);
        });
        return finalMap;


    }

    public void someoneGetBonus(String ip, List<CardType> cardsList, Integer bonusArmies, Integer extraBonusArmies) {
        JOptionPane.showMessageDialog(this, "Player " + ip + " get bonus of " + bonusArmies + " armies for " + cardsList.toString() + " cards and " + extraBonusArmies + " extra armies", "Bonus", JOptionPane.INFORMATION_MESSAGE);
    }

    public void receiveAttackMsg(String ipClientAttack, String ipClientDefend, List<Integer> diceATKResult, Territory territory) {
    }

    public void receiveDefendMsg(String ipClientAttack, String ipClientDefend, List<Integer> diceDEFResult, Territory territory) {
    }

    public void someoneDrawStateCard(String ip) {
    }

    public void someoneWin(String ip, Goal goalCard, List<Territory> listTerritories) {
    }

    public void placeArmies() {
        SwingUtilities.invokeLater(() -> {
            this.state.set(GAME_STATE.PLACING);
            var ps = ((GameClient) Launcher.getCurrentClient()).getPlacingState();
            this.jlState.setText("Placing armies: " + ps.getFirst() + " :/: " + ps.getSecond());
            this.updateMapImage();
            this.enableActions();
        });
    }

    public void attackPhase() {
        SwingUtilities.invokeLater(() -> {
            this.state.set(GAME_STATE.ATTACKING);
            this.jlState.setText("Attack phase");
        });
    }

    public void waitPhase() {
        SwingUtilities.invokeLater(() -> {
            this.state.set(GAME_STATE.WAITING);
            this.disableActions();
            this.jlState.setText("WAITING");
        });
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

    public void updateMapImage() {
        final AtomicReference<BufferedImage> img = new AtomicReference<>();
        try {
            img.set(ImageIO.read(new File(Paths.get("src/main/java/assets/image/map.png").toAbsolutePath().toString())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        };
        Graphics2D g2d = img.get().createGraphics();

        Map<JSONClient, Integer> clients = new HashMap<>();
        parseJsonMap().forEach((country, coords) -> {
            Polygon p = new Polygon();
            coords.forEach((pair) ->
                    p.addPoint(pair.getX(), pair.getY()-((map.getHeight()-img.get().getHeight())/2))
            );
            var client = ((GameClient)Launcher.getCurrentClient());
            Territory ter = Territory.fromString(country);
            JSONClient clt = client.getAllTerritories()
                    .keySet().stream().filter(pa -> pa.getSecond().name().equals(ter.name()))
                    .map(Pair::getFirst).collect(Collectors.toList()).get(0);
            Integer armies = client.getAllTerritories().get(new Pair<>(clt, ter));
            if(!clients.containsKey(clt)){
                clients.put(clt, clients.size());
            }
            Color color = this.colors.get(clients.get(clt));

            g2d.setColor(color);
            //g2d.drawPolygon(p);
            g2d.fillOval((int)p.getBounds().getCenterX()-15, (int)p.getBounds().getCenterY()-15, 30, 30);
            g2d.setColor(Color.BLACK);
            g2d.drawString(armies.toString(), (int)p.getBounds().getCenterX()-5, (int)p.getBounds().getCenterY()+5);
        });
        g2d.dispose();
        map.setIcon(new ImageIcon(img.get()));
    }
}
