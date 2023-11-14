package app.game.GUI;

import app.Launcher;
import app.common.Pair;
import app.game.GameClient;
import app.game.GameClientImpl;
import app.game.card.CardType;
import app.game.card.Goal;
import app.game.card.Territory;
import app.lobbySelector.JSONClient;
import app.manager.gui.GUI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
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


    protected final AtomicReference<GAME_STATE> state;
    private final GUIGame guiGame;
    private final List<Color> colors = List.of(
            Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN, Color.PINK, Color.GRAY, Color.BLUE, Color.WHITE
    );
    private final JLabel map;
    private final List<Pair<String, Integer>> clientColorsList = new ArrayList<>();
    private final List<String> enemies;
    private final JLabel jlState;
    private final JTextArea log = new JTextArea();
    private boolean orderHasBeenSet = false;
    private JLabel player;
    private JLabel enemiesLabel;
    private JButton endTurnButton;
    private JButton attackButton;
    private JButton moveTroopsButton;
    private Territory territoryFromToAttack;
    private Territory territoryToAttack;

    public GUIGame() {
        setLayout(new BorderLayout());
        enemies = new ArrayList<>();
        this.jlState = new JLabel("WAITING");
        map = new JLabel();
        map.setIcon(new ImageIcon(Paths.get("src/main/java/assets/image/map.png").toAbsolutePath().toString()));
        this.add(map, BorderLayout.CENTER);
        map.addMouseListener(onMapClick());
        this.state = new AtomicReference<>(GAME_STATE.WAITING);
        this.guiGame = this;
        this.setTopPanel();
        this.setBottomPanel();
        this.setRightPanel();
        this.disableActions();
    }

    @Override
    public String getTitle() {
        return "RiSiKo!!!";
    }

    private void setRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(Box.createVerticalGlue());
        endTurnButton = new JButton("End Turn");
        rightPanel.add(endTurnButton);
        rightPanel.add(Box.createVerticalGlue());
        attackButton = new JButton("Attack Phase");
        rightPanel.add(attackButton);
        rightPanel.add(Box.createVerticalGlue());
        moveTroopsButton = new JButton("Move Troops Phase");
        rightPanel.add(moveTroopsButton);
        rightPanel.add(Box.createVerticalGlue());

        endTurnButton.addActionListener(e -> {
            ((GameClientImpl) Launcher.getCurrentClient()).endMyTurn();
            this.waitingPhase();
        });
        attackButton.addActionListener(e -> {
            this.attackPhase();
        });
        moveTroopsButton.addActionListener(e -> {
            this.movingPhase();
        });

        this.add(rightPanel, BorderLayout.EAST);
    }

    private void setTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        player = new JLabel("Player: " + ((GameClientImpl) Launcher.getCurrentClient()).getNickname());
        topPanel.add(player);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(new JLabel("Goal: " + ((GameClientImpl) Launcher.getCurrentClient()).getGoal()));
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(this.jlState);
        topPanel.add(Box.createHorizontalGlue());
        ((GameClientImpl) Launcher.getCurrentClient()).getClientList().forEach((JSONClient client) -> {
            if (!client.getNickname().equals(((GameClientImpl) Launcher.getCurrentClient()).getNickname())) {
                enemies.add(client.getNickname());
            }
        });
        enemiesLabel = new JLabel("Enemies: " + enemies.toString());
        topPanel.add(enemiesLabel);
        topPanel.add(Box.createHorizontalGlue());
        add(topPanel, BorderLayout.NORTH);
    }

    private void setBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        log.setEditable(false);
        log.setVisible(true);
        log.append("LOG:");
        bottomPanel.add(log);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void addLogToTextArea(String log) {
        SwingUtilities.invokeLater(() -> {
            this.log.append(log);
        });
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
                        System.out.println("State: " + state.get());
                        switch (state.get()) {
                            case PLACING:
                                countryClickedWhilePlacing(e, country);
                                break;
                            case ATTACKING_SELECT_FIRST_COUNTRY:
                                countryClickedWhileAttackingFirstCountry(e, country);
                                break;
                            case ATTACKING_SELECT_SECOND_COUNTRY:
                                countryClickedWhileAttackingSecondCountry(e, country);
                                break;
                            case WAITING:
                            case ORDERING:
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

    private void countryClickedWhileAttackingSecondCountry(MouseEvent e, String country) {
        if (territoryFromToAttack.getNeighbours().contains(country)) {
            var bool = ((GameClientImpl) Launcher.getCurrentClient()).getAllTerritories().entrySet().stream()
                    .filter(p -> p.getKey().getSecond().equals(Territory.fromString(country)))
                    .collect(Collectors.toList()).get(0)
                    .getKey()
                    .getFirst()
                    .getNickname()
                    .equals(((GameClientImpl) Launcher.getCurrentClient()).getNickname());
            if (!bool) {
                territoryToAttack = Territory.fromString(country);
                Object nDicesToUse = (JOptionPane.showInputDialog(guiGame, "How many dices do you want to use? (1-3)", "Dices Selection", JOptionPane.QUESTION_MESSAGE, null, getNDicesFromTerritory(territoryFromToAttack, "ATTACK"), 0));
                System.out.println("nDices for attack: " + nDicesToUse);
                if (nDicesToUse == null || (Integer) nDicesToUse == 0) {
                    JOptionPane.showMessageDialog(guiGame, "You must select at least one dice", "Error", JOptionPane.ERROR_MESSAGE);
                } else if ((Integer) nDicesToUse > 0) {
                    ((GameClient) Launcher.getCurrentClient()).sendAttackMsg(territoryFromToAttack, territoryToAttack, (Integer) nDicesToUse);
                    this.waitingPhase();
                }
            }
        }
    }

    private Integer[] getNDicesFromTerritory(Territory territory, String type) {

        var nArmies = ((GameClientImpl) Launcher.getCurrentClient())
                .getAllTerritories()
                .entrySet().stream()
                .filter(p -> p.getKey().getSecond().equals(territory))
                .collect(Collectors.toList())
                .get(0)
                .getValue();

        System.out.println("nArmies in the clicked country " + nArmies);

        if (type.equals("ATTACK")) {
            switch (nArmies) {
                case 0:
                case 1:
                    return new Integer[]{0};
                case 2:
                    return new Integer[]{0, 1};
                case 3:
                    return new Integer[]{0, 1, 2};
                default:
                    return new Integer[]{0, 1, 2, 3};
            }
        } else if (type.equals("DEFEND")) {
            switch (nArmies) {
                case 0:
                    return new Integer[]{0};
                case 1:
                    return new Integer[]{0, 1};
                case 2:
                    return new Integer[]{0, 1, 2};
                default:
                    return new Integer[]{0, 1, 2, 3};
            }
        }
        return new Integer[0];
    }


    private void countryClickedWhileAttackingFirstCountry(MouseEvent e, String country) {
        ((GameClientImpl) Launcher.getCurrentClient()).getAllTerritories().forEach((pair, armies) -> {
            if (pair.getFirst().getNickname().equals(((GameClientImpl) Launcher.getCurrentClient()).getNickname())) {
                if (pair.getSecond().equals(Territory.fromString(country))) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        territoryFromToAttack = Territory.fromString(country);
                        this.state.set(GAME_STATE.ATTACKING_SELECT_SECOND_COUNTRY);
                        this.jlState.setText("Attack phase: Click on one of the near enemies territory to attack");
                    }
                }
            }
        });
    }

    private void countryClickedWhilePlacing(java.awt.event.MouseEvent e, String country) {
        ((GameClientImpl) Launcher.getCurrentClient()).getAllTerritories().forEach((pair, armies) -> {
            if (pair.getFirst().getNickname().equals(((GameClientImpl) Launcher.getCurrentClient()).getNickname())) {
                if (pair.getSecond().equals(Territory.fromString(country))) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        placeArmy(country, 1);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        placeArmy(country, -1);
                    }
                }
            }
        });
    }

    private void placeArmy(String country, Integer deltaArmies) {
        Launcher.getVertx().setTimer(250, id -> {
            ((GameClient) Launcher.getCurrentClient()).placeArmy(country, deltaArmies);
            var ps = ((GameClient) Launcher.getCurrentClient()).getPlacingState();
            if (this.state.get() == GAME_STATE.PLACING) {
                this.jlState.setText("Placing armies: " + (ps.getFirst()) + " :/: " + ps.getSecond());
                System.out.println("Placing armies: " + (ps.getFirst()) + " :/: " + ps.getSecond());
            }
        });
        updateMapImage();
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
                //WINDOW is 1600x1000 +185 is magic number for fixing the map because is centered in the JFRAME
                PairOfCoordinates p1 = new PairOfCoordinates(coords.get(i), coords.get(i + 1) + 185);
                l.add(p1);
            }
            finalMap.put(country, l);
        });
        return finalMap;


    }

    public void someoneGetBonus(String ip, List<CardType> cardsList, Integer bonusArmies, Integer extraBonusArmies) {
        JOptionPane.showMessageDialog(this, "Player " + ip + " get bonus of " + bonusArmies + " armies for " + cardsList.toString() + " cards and " + extraBonusArmies + " extra armies", "Bonus", JOptionPane.INFORMATION_MESSAGE);
    }

    public void receiveAttackMsg(String ipClientAttack, String ipClientDefend, List<Integer> diceATKResult, Territory enemyTerritory, Territory myTerritory) {
        SwingUtilities.invokeLater(() -> {
            this.enableActions();
            String playerAttacker = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> c.getIP().equals(ipClientAttack)).collect(Collectors.toList()).get(0).getNickname();
            JOptionPane.showMessageDialog(this, "Player " + playerAttacker + " attack " + myTerritory.name() + " from " + enemyTerritory.name() + " with dices result: " + diceATKResult, "Incoming Attack!!!", JOptionPane.INFORMATION_MESSAGE);
            //ROLL DICES TO DEFEND!!!
            Object nDicesToUse = (JOptionPane.showInputDialog(guiGame, "How many dices do you want to use? (0-3)", "Dices Selection", JOptionPane.QUESTION_MESSAGE, null, getNDicesFromTerritory(myTerritory, "DEFEND"), 0));
            System.out.println("nDices used for defending: " + nDicesToUse);
//        if (nDicesToUse == 0) {
//            JOptionPane.showMessageDialog(guiGame, "You must select at least one dice", "Error", JOptionPane.ERROR_MESSAGE);
//        } else if (nDicesToUse > 0) {
//            ((GameClient) Launcher.getCurrentClient()).sendDefendMsg(enemyTerritory, myTerritory, nDicesToUse);
//            this.waitingPhase();
//        }
            ((GameClient) Launcher.getCurrentClient()).sendDefendMsg(enemyTerritory, myTerritory, nDicesToUse == null ? 0 : (Integer) nDicesToUse);
        });

    }

    public void receiveDefendMsg(String ipClientAttack, String ipClientDefend, List<Integer> diceDEFResult, Territory myTerritory, Territory enemyTerritory) {
        String playerDefender = ((GameClientImpl) Launcher.getCurrentClient()).getClientList().stream().filter(c -> c.getIP().equals(ipClientDefend)).collect(Collectors.toList()).get(0).getNickname();
        JOptionPane.showMessageDialog(this, "Player " + playerDefender + " defend " + enemyTerritory.name() + " from the attack of " + enemyTerritory.name() + " with dices result: " + diceDEFResult, "Defence result!!!", JOptionPane.INFORMATION_MESSAGE);
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
            this.disableAllButtons();
        });
    }

    public void updateMapImage() {
        SwingUtilities.invokeLater(() -> {
            clientColorsList.clear();
            final AtomicReference<BufferedImage> img = new AtomicReference<>();
            try {
                img.set(ImageIO.read(new File(Paths.get("src/main/java/assets/image/map.png").toAbsolutePath().toString())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Graphics2D g2d = img.get().createGraphics();

            Map<JSONClient, Integer> clients = new HashMap<>();
            parseJsonMap().forEach((country, coords) -> {
                Polygon p = new Polygon();
                coords.forEach((pair) ->
                        p.addPoint(pair.getX(), pair.getY() - ((map.getHeight() - img.get().getHeight()) / 2))
                );
                var client = ((GameClient) Launcher.getCurrentClient());
                Territory ter = Territory.fromString(country);
                JSONClient clt = client.getAllTerritories()
                        .keySet().stream().filter(pa -> pa.getSecond().name().equals(ter.name()))
                        .map(Pair::getFirst).collect(Collectors.toList()).get(0);
                Integer armies = client.getAllTerritories().get(new Pair<>(clt, ter));
                if (!clients.containsKey(clt)) {
                    clients.put(clt, clients.size());
                }
                Color color = this.colors.get(clients.get(clt));
                clientColorsList.add(new Pair<>(clt.getNickname(), clients.get(clt)));
                g2d.setColor(color);
                //g2d.drawPolygon(p);
                g2d.fillOval((int) p.getBounds().getCenterX() - 15, (int) p.getBounds().getCenterY() - 15, 30, 30);
                g2d.setColor(Color.BLACK);
                g2d.drawString(armies.toString(), (int) p.getBounds().getCenterX() - 5, (int) p.getBounds().getCenterY() + 5);
            });
            Integer indexOfColor = clientColorsList.stream().filter(p -> p.getFirst().equals(((GameClientImpl) Launcher.getCurrentClient()).getNickname())).collect(Collectors.toList()).get(0).getSecond();
            player.setText("Player: " + ((GameClientImpl) Launcher.getCurrentClient()).getNickname() + " has color: " + getColorFromIndex(indexOfColor));
            StringBuilder printEnemies = new StringBuilder();
            for (String enemy : enemies) {
                printEnemies.append(enemy.concat(" has color: ").concat(getColorFromIndex(clientColorsList.stream().filter(p -> p.getFirst().equals(enemy)).collect(Collectors.toList()).get(0).getSecond())).concat(" \n"));
            }
            enemiesLabel.setText("Enemies: " + printEnemies);
            g2d.dispose();
            map.setIcon(new ImageIcon(img.get()));
        });

    }

    private String getColorFromIndex(Integer indexOfColor) {
        switch (indexOfColor) {
            case 0:
                return "GREEN";
            case 1:
                return "RED";
            case 2:
                return "YELLOW";
            case 3:
                return "CYAN";
            case 4:
                return "PINK";
            case 5:
                return "GRAY";
            case 6:
                return "BLUE";
            case 7:
                return "WHITE";
            default:
                return "BLACK";
        }
    }

    public boolean orderFoundAndTurnCheck() {
        this.orderHasBeenSet = true;
        //Check if I'm the first player to play
        var firstNick = ((GameClientImpl) Launcher.getCurrentClient()).getRandomOrder().get(0).getNickname();
        System.out.println("First player to play: " + firstNick);
        var nick = ((GameClientImpl) Launcher.getCurrentClient()).getNickname();
        System.out.println("My nick: " + nick);
        return firstNick.equals(nick);
    }

    public void startTurn() {
        SwingUtilities.invokeLater(() -> {
            //PLACE N-ARMIES BASED ON OWN TERRITORIES
            this.enableActions();
            double count = (double) ((GameClient) Launcher.getCurrentClient())
                    .getAllTerritories()
                    .keySet()
                    .stream()
                    .filter(p -> p.getFirst().getNickname().equals(((GameClientImpl) Launcher.getCurrentClient()).getNickname())).count();
            count = count / ((GameClientImpl) Launcher.getCurrentClient()).getClientList().size();

            ((GameClient) Launcher.getCurrentClient()).placeArmies(((int) Math.floor(count)));

        });
    }

    public void attackPhase() {
        SwingUtilities.invokeLater(() -> {
            this.state.set(GAME_STATE.ATTACKING_SELECT_FIRST_COUNTRY);
            this.jlState.setText("Attack phase: Click on one of your territory from where to start the attack");
        });
    }

    public void movingPhase() {
        SwingUtilities.invokeLater(() -> {
            this.state.set(GAME_STATE.MOVING_SELECT_FIRST_COUNTRY);
            this.jlState.setText("Moving phase");
        });
    }

    public void playingPhase() {
        SwingUtilities.invokeLater(() -> {
            this.enableActions();
            this.state.set(GAME_STATE.PLAYING);
            this.jlState.setText("Playing phase");
        });
    }

    public void waitingPhase() {
        SwingUtilities.invokeLater(() -> {
            this.state.set(GAME_STATE.WAITING);
            this.jlState.setText("WAITING");
            this.disableActions();
        });
    }

    private void disableActions() {
        SwingUtilities.invokeLater(() -> {
            this.setEnabled(false);
            this.disableAllButtons();
            this.repaint();
            this.revalidate();
        });
    }

    private void enableActions() {
        SwingUtilities.invokeLater(() -> {
            this.setEnabled(true);
            this.enableAllButtons();
            this.repaint();
            this.revalidate();
        });
    }

    public void disableAllButtons() {
        SwingUtilities.invokeLater(() -> {
            attackButton.setEnabled(false);
            moveTroopsButton.setEnabled(false);
            endTurnButton.setEnabled(false);
        });
    }

    private void enableAllButtons() {
        SwingUtilities.invokeLater(() -> {
            attackButton.setEnabled(true);
            moveTroopsButton.setEnabled(true);
            endTurnButton.setEnabled(true);
        });
    }

    public void orderingPhase() {
        SwingUtilities.invokeLater(() -> {
            if (!orderHasBeenSet) {
                disableAllButtons();
                this.state.set(GAME_STATE.ORDERING);
                this.jlState.setText("ORDERING");
                var listToShuffle = new ArrayList<>(((GameClientImpl) Launcher.getCurrentClient()).getClientList());
                System.out.println("List to shuffle: " + listToShuffle);
                Collections.shuffle(listToShuffle);
                ((GameClientImpl) Launcher.getCurrentClient()).sendRandomOrderForTurning(listToShuffle);
            }
        });
    }

    public enum GAME_STATE {WAITING, ORDERING, PLACING, ATTACKING_SELECT_FIRST_COUNTRY, ATTACKING_SELECT_SECOND_COUNTRY, MOVING_SELECT_FIRST_COUNTRY, MOVING_SELECT_SECOND_COUNTRY, PLAYING}

    private static class PairOfCoordinates {
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
