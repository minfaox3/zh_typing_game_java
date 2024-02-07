package com.zh_typing_game;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class Game extends JFrame implements KeyListener {
    private JPanel panelMain;
    private JLabel labelContent;
    private JComboBox comboBoxContent;
    private JButton buttonContentFileGetter;
    private JLabel labelMode;
    private JComboBox comboBoxMode;
    private JCheckBox checkBoxPinyinVisible;
    private JCheckBox checkBoxAnswerVisible;
    private JCheckBox checkBoxMeaningVisible;
    private JButton buttonStartGame;
    private JLabel labelTime;
    private JLabel labelScore;
    private JLabel labelMeaning;
    private JLabel labelWord;
    private JLabel labelPinyin;
    private JLabel labelType;
    private JPanel panelContent;
    private JPanel panelMode;
    private JPanel panelSettings;
    private JLabel labelStatus;
    private JLabel label1;
    private JLabel labelSelectedFile;
    private JButton buttonStopGame;
    private JPanel panelControl;
    private JLabel labelAnswer;
    private JPanel panelGame;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItemAbout;
    private JMenuItem menuItemExit;

    private FileDialog fileDialog;

    private ArrayList<Word> wordList = new ArrayList<>();
    private ArrayList<Word> typedWordList = new ArrayList<>();

    private Word selectedWord;

    private ArrayList<String> pinyin = new ArrayList<>();
    private ArrayList<String> answer = new ArrayList<>();

    private boolean is_gaming = false;
    private boolean is_proposing = false;

    private Random rand = new Random();

    private int score = 0;
    private int count_c = 0;
    private int count_m = 0;
    private int correct_type_word = 0;
    private int correct_type_pinyin = 0;
    private int correct_type_word_pinyin = 0;
    private int correct_type_char = 0;

    private float time = 0;
    private final Timer timer;

    private final String ID_LOAD_FILE = "ファイル読み込み";
    private final String ID_HSK_ONE = "HSK1級";
    private final String ID_INFINITY_R = "無限（ランダム）";
    private final String ID_ONE_MINUTE_R = "1分（ランダム）";
    private final String ID_ONE_LAP = "1周";

    private void printError(String str) {
        labelStatus.setText("【エラー】" + str);
    }

    private void printInfo(String str) {
        labelStatus.setText("【情報】" + str);
    }

    private boolean loadWordCSV(String path) {
        wordList.clear();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] items = line.split(",");
                if (items.length >= 4) {
                    Word word = new Word(items[0], items[1].split("\\|"), items[2].split("\\|"), items[3]);
                    if (word.word.length() == word.pinyin.length && String.join("", word.pinyin).length() == word.answer.length) {
                        wordList.add(word);
                    }
                }
            }
            if (wordList.isEmpty()) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private boolean selectWord() {
        if (comboBoxMode.getSelectedItem() == ID_ONE_LAP) {
            if (typedWordList.size() >= wordList.size()) {
                return false;
            } else {
                selectedWord = wordList.get(typedWordList.size());
            }
        } else {
            selectedWord = wordList.get(rand.nextInt(wordList.size()));
        }
        return true;
    }

    private void propose() {
        is_proposing = true;
        if (!selectWord()) {
            buttonStopGame.doClick();
            return;
        }

        correct_type_word = 0;
        correct_type_word_pinyin = 0;
        correct_type_pinyin = 0;
        correct_type_char = 0;

        if (checkBoxMeaningVisible.isSelected()) {
            labelMeaning.setText(selectedWord.meaning);
        } else {
            labelMeaning.setText(" ");
        }
        if (checkBoxPinyinVisible.isSelected()) {
            labelPinyin.setText(String.join("", selectedWord.pinyin));
        } else {
            labelPinyin.setText(" ");
        }
        if (checkBoxAnswerVisible.isSelected()) {
            labelAnswer.setText(String.join("", selectedWord.answer));
        } else {
            labelAnswer.setText(" ");
        }

        labelWord.setText(selectedWord.word);
        labelType.setText(" ");
        is_proposing = false;
    }

    private void updateTimer() {
        if (comboBoxMode.getSelectedItem() == ID_ONE_MINUTE_R) {
            time -= 0.1f;
            if (time <= 0) {
                timer.stop();
                time = 0;
                buttonStopGame.doClick();
            }
        } else {
            time += 0.1f;
        }
        labelTime.setText(String.format("%1$.1f", time));
    }

    public Game() {
        menuBar = new JMenuBar();
        menu = new JMenu("メニュー");
        menuItemAbout = new JMenuItem("バージョン・著作権情報");
        menuItemExit = new JMenuItem("終了");
        menu.add(menuItemAbout);
        menu.add(menuItemExit);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        menuItemAbout.addActionListener(e -> JOptionPane.showMessageDialog(null, "中国語タイピングゲーム v1.0.0\n©2023-2024 minfaox3", "バージョン・著作権情報", JOptionPane.INFORMATION_MESSAGE));

        menuItemExit.addActionListener(e -> System.exit(0));

        comboBoxContent.addActionListener(e -> {
            if (comboBoxContent.getSelectedItem() == ID_LOAD_FILE) {
                buttonContentFileGetter.setEnabled(true);
                buttonStartGame.setEnabled(false);
            } else {
                buttonContentFileGetter.setEnabled(false);
                labelSelectedFile.setText("未選択");

                if (loadWordCSV(new File(Game.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + "\\hsk1.csv")) {
                    printInfo("HSK1級の問題データの読み込みに成功しました。");
                    buttonStartGame.setEnabled(true);
                } else {
                    printError("HSK1級の問題データの読み込みに失敗しました。");
                    buttonStartGame.setEnabled(false);
                }
            }
        });

        buttonContentFileGetter.addActionListener(e -> {
            fileDialog.setVisible(true);
            if (fileDialog.getFile() != null) {
                File file = new File(fileDialog.getDirectory(), fileDialog.getFile());
                printInfo(file.getAbsolutePath() + "の読み込み開始");
                if (loadWordCSV(file.getAbsolutePath())) {
                    labelSelectedFile.setText(file.getName());
                    buttonStartGame.setEnabled(true);
                    printInfo(wordList.size() + "個の単語の読み込み完了");
                } else {
                    labelSelectedFile.setText("未選択");
                    printError(file.getAbsolutePath() + "の読み込み失敗");
                }
            }
        });

        buttonStartGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxContent.setEnabled(false);
                comboBoxMode.setEnabled(false);
                buttonContentFileGetter.setEnabled(false);
                buttonStartGame.setEnabled(false);
                checkBoxAnswerVisible.setEnabled(false);
                checkBoxPinyinVisible.setEnabled(false);
                checkBoxMeaningVisible.setEnabled(false);

                score = 0;

                if (comboBoxMode.getSelectedItem() == ID_ONE_MINUTE_R) {
                    time = 60;
                } else {
                    time = 0;
                }
                correct_type_word = 0;
                correct_type_word_pinyin = 0;
                correct_type_pinyin = 0;
                correct_type_char = 0;

                count_c = 0;
                count_m = 0;

                typedWordList.clear();

                labelScore.setText(String.valueOf(score));
                labelTime.setText(String.valueOf(time));
                labelMeaning.setText(" ");
                labelPinyin.setText(" ");
                labelAnswer.setText(" ");
                labelWord.setText(" ");
                labelPinyin.setText(" ");

                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearFocusOwner();

                is_gaming = true;
                buttonStopGame.setEnabled(true);
                requestFocus();
                timer.start();
                propose();
            }
        });

        buttonStopGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonStopGame.setEnabled(false);

                is_gaming = false;
                timer.stop();

                StringBuilder result_text = new StringBuilder();
                result_text.append("スコア：").append(score).append("\n");
                double wpm = typedWordList.size();
                double cpm = count_c;
                double time_e = 60;
                if (comboBoxMode.getSelectedItem() != ID_ONE_MINUTE_R) {
                    wpm = typedWordList.size() / (time / 60f);
                    cpm = count_c / (time / 60f);
                    time_e = time;
                }
                result_text.append("学習時間：").append(String.format("%1$.1f", time_e)).append("秒\n");
                result_text.append("WPM：").append(String.format("%1$.0f", wpm)).append("\n");
                result_text.append("CPM：").append(String.format("%1$.0f", cpm)).append("\n");
                result_text.append("タイプ単語数：").append(typedWordList.size()).append("\n");
                result_text.append("正解タイプ数：").append(count_c).append("\n");
                result_text.append("間違いタイプ数：").append(count_m).append("\n");
                JOptionPane.showMessageDialog(null, result_text.toString(), "リザルト", JOptionPane.INFORMATION_MESSAGE);

                comboBoxContent.setEnabled(true);
                comboBoxMode.setEnabled(true);
                if (comboBoxContent.getSelectedItem() == ID_LOAD_FILE) {
                    buttonContentFileGetter.setEnabled(true);
                }
                buttonStartGame.setEnabled(true);
                checkBoxAnswerVisible.setEnabled(true);
                checkBoxPinyinVisible.setEnabled(true);
                checkBoxMeaningVisible.setEnabled(true);
            }
        });

        fileDialog = new FileDialog(this, "単語csvファイルの選択");
        labelStatus.setBorder(new EtchedBorder(EtchedBorder.RAISED, Color.WHITE, Color.BLACK));

        labelTime.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        labelScore.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        labelMeaning.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        labelPinyin.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        labelAnswer.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        labelWord.setFont(new Font("微软雅黑", Font.BOLD, 50));
        labelType.setFont(new Font(Font.DIALOG, Font.BOLD, 30));

        buttonContentFileGetter.setBackground(new Color(51, 172, 255));
        buttonContentFileGetter.setForeground(Color.WHITE);
        buttonContentFileGetter.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        buttonContentFileGetter.setBorder(null);
        buttonContentFileGetter.setEnabled(false);

        buttonStartGame.setBackground(new Color(51, 172, 255));
        buttonStartGame.setForeground(Color.WHITE);
        buttonStartGame.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        buttonStartGame.setBorder(null);
        buttonStartGame.setEnabled(false);
        buttonStopGame.setBackground(new Color(255, 110, 30));
        buttonStopGame.setForeground(Color.WHITE);
        buttonStopGame.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        buttonStopGame.setBorder(null);
        buttonStopGame.setEnabled(false);

        comboBoxContent.setBackground(Color.WHITE);
        comboBoxContent.setModel(new DefaultComboBoxModel(new String[]{ID_HSK_ONE, ID_LOAD_FILE}));
        comboBoxContent.setSelectedItem(ID_HSK_ONE);

        comboBoxMode.setBackground(Color.WHITE);
        comboBoxMode.setModel(new DefaultComboBoxModel(new String[]{ID_INFINITY_R, ID_ONE_MINUTE_R, ID_ONE_LAP}));

        timer = new Timer(100, e -> updateTimer());

        setContentPane(panelMain);
        setTitle("中国語タイピングゲーム");
        setSize(700, 550);
        setMinimumSize(new Dimension(700, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (is_gaming && !is_proposing) {
            StringBuilder html;
            if (e.getKeyChar() == selectedWord.answer[correct_type_pinyin].charAt(correct_type_char)) {
                count_c++;
                labelType.setText(labelType.getText() + selectedWord.answer[correct_type_pinyin].charAt(correct_type_char));

                //make answer text
                if (checkBoxAnswerVisible.isSelected()) {
                    html = new StringBuilder();
                    html.append("<html><body><span style=\"color:#0000FF;\">");
                    for (int y = 0; y < selectedWord.answer.length; y++) {
                        for (int x = 0; x < selectedWord.answer[y].length(); x++) {
                            html.append(selectedWord.answer[y].charAt(x));
                            if (y == correct_type_pinyin && x == correct_type_char) {
                                html.append("</span>");
                            }
                        }
                    }
                    html.append("</body></html>");
                    labelAnswer.setText(html.toString());
                }

                correct_type_char++;

                if (correct_type_char == selectedWord.answer[correct_type_pinyin].length()) {
                    labelType.setText(labelType.getText().substring(0, labelType.getText().length() - correct_type_char) + selectedWord.pinyin[correct_type_word].charAt(correct_type_word_pinyin));

                    //make pinyin text
                    if (checkBoxPinyinVisible.isSelected()) {
                        html = new StringBuilder();
                        html.append("<html><body><span style=\"color:#0000FF;\">");
                        for (int y = 0; y < selectedWord.pinyin.length; y++) {
                            for (int x = 0; x < selectedWord.pinyin[y].length(); x++) {
                                html.append(selectedWord.pinyin[y].charAt(x));
                                if (y == correct_type_word && x == correct_type_word_pinyin) {
                                    html.append("</span>");
                                }
                            }
                        }
                        html.append("</body></html>");
                        labelPinyin.setText(html.toString());
                    }

                    correct_type_pinyin++;
                    correct_type_word_pinyin++;
                    correct_type_char = 0;

                    if (correct_type_word_pinyin == selectedWord.pinyin[correct_type_word].length()) {
                        //make word text
                        html = new StringBuilder();
                        html.append("<html><body><span style=\"color:#0000FF;\">");
                        for (int x = 0; x < selectedWord.word.length(); x++) {
                            html.append(selectedWord.word.charAt(x));
                            if (x == correct_type_word) {
                                html.append("</span>");
                            }
                        }
                        html.append("</body></html>");
                        labelWord.setText(html.toString());
                        correct_type_word++;
                        correct_type_word_pinyin = 0;

                        if (correct_type_word == selectedWord.word.length()) {
                            typedWordList.add(selectedWord);
                            propose();
                        }
                    }
                }
                score++;
            } else {
                count_m++;
                //make answer text
                if (checkBoxAnswerVisible.isSelected()) {
                    html = new StringBuilder();
                    html.append("<html><body><span style=\"color:#0000FF;\">");
                    for (int y = 0; y < selectedWord.answer.length; y++) {
                        for (int x = 0; x < selectedWord.answer[y].length(); x++) {
                            if (y == correct_type_pinyin && x == correct_type_char) {
                                html.append("</span><span style=\"color:#FF0000;\">").append(selectedWord.answer[y].charAt(x)).append("</span>");
                            } else {
                                html.append(selectedWord.answer[y].charAt(x));
                            }
                        }
                    }
                    html.append("</body></html>");
                    labelAnswer.setText(html.toString());
                }

                //make pinyin text
                if (checkBoxPinyinVisible.isSelected()) {
                    html = new StringBuilder();
                    html.append("<html><body><span style=\"color:#0000FF;\">");
                    for (int y = 0; y < selectedWord.pinyin.length; y++) {
                        for (int x = 0; x < selectedWord.pinyin[y].length(); x++) {
                            if (y == correct_type_word && x == correct_type_word_pinyin) {
                                html.append("</span><span style=\"color:#FF0000;\">").append(selectedWord.pinyin[y].charAt(x)).append("</span>");
                            } else {
                                html.append(selectedWord.pinyin[y].charAt(x));
                            }
                        }
                    }
                    html.append("</body></html>");
                    labelPinyin.setText(html.toString());
                }

                //make word text
                html = new StringBuilder();
                html.append("<html><body><span style=\"color:#0000FF;\">");
                for (int x = 0; x < selectedWord.word.length(); x++) {
                    if (x == correct_type_word) {
                        html.append("</span><span style=\"color:#FF0000;\">").append(selectedWord.word.charAt(x)).append("</span>");
                    } else {
                        html.append(selectedWord.word.charAt(x));
                    }
                }
                html.append("</body></html>");
                labelWord.setText(html.toString());

                score--;
            }

            labelScore.setText(String.valueOf(score));
            if (score >= 0) {
                labelScore.setForeground(Color.BLUE);
            } else {
                labelScore.setForeground(Color.RED);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setVisible(true);
    }

}
