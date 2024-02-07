# 中国語タイピングゲーム(Java版)
[中国語タイピングゲーム(Web版)](https://zh-typing-game.minfaox3.net/)のJava移植版です。  
ローカル環境でタイピングできます。（一部動作は変更されています。）

![thumb](https://github.com/minfaox3/zh_typing_game_java/blob/master/thumb.gif?raw=true)

## 開発環境
* JDK 16
* Java（Swing）
* IntelliJ IDEA 2023.3 Ultimate Edition

## 入方方法
### リリースビルドをダウンロードする場合
1. [https://github.com/minfaox3/zh_typing_game_java/releases/tag/v1.0.0](https://github.com/minfaox3/zh_typing_game_java/releases/tag/v1.0.0)で`zh_typing_game_jar.zip`をダウンロードする。
2. 解凍する。
    * Javaが入っていない場合は導入してください。

### ビルドする場合
成果物としてjarファイルを作成することを推奨します。  
またデフォルトで読み込ませる[HSK1級のCSVファイル](https://minfaox3.github.io/m-storage/hsk1.csv)をjarファイルと同じディレクトリに置くことを推奨します。  
置いておくとすぐにHSK1級を試すことができます。  
置かない場合は自分で用意したCSVファイルを起動後に読み込ませてください。

### プレイ方法
![view](https://github.com/minfaox3/zh_typing_game_java/blob/master/view.png?raw=true)
#### 内容選択
「内容：」のドロップダウンボックスから「HSK1級」か「ファイル読み込み」を選択することができます。  
「ファイル読み込み」を選択すると右側の「ファイル参照」ボタンが有効化されるのでここで使用する自前のCSVファイルを選択することができます。  
選択できたら「選択ファイル：」そのファイルが表示されます。  
読み込みに成功した場合は画面最下部のステータスバーに「【情報】〇個の単語の読み込み完了」と表示されています。

＞読み込めるCSV形式
```
単語や熟語,ピンイン,正解タイプ文字列,意味,備考（今後のアップデートで解説など使用可能性あり）
```
「ピンイン」は「単語や熟語」1文字ごとに「|」で区切ってください。
「正解タイプ文字列」は「ピンイン」1文字ごとに「|」で区切ってください。

例：
```
打电话,dā|diàn|huà,d|a1|d|i|a4|n|h|u|a4,電話をかける,
```

#### モード選択
モードは
* 無限（ランダム）
* 1分（ランダム）
* 1周

から選択することができます。  
「無限」及び「1周」は好きなタイミングで「中止」ボタンを押していただければ終了できます。
「1分」は1分たつと自動で終了となりますが「中止」ボタンで中止することも可能です

#### 表示情報設定
* ピンイン
* 正解
* 意味

についてはそれぞれのチェックボックスを付けはずしすることで表示非表示を切り替えることができます。  
プレイ中は変更できないので注意してください。

#### プレイ開始
「開始」ボタンをクリックすることでプレイ可能です。

#### リザルト
![result](https://github.com/minfaox3/zh_typing_game_java/blob/master/result.png?raw=true)

「中止」時もしくは終了時に自動でリザルトが表示されます。

## ライセンス
本ソフトウェアは[MITライセンス](https://github.com/minfaox3/zh_typing_game_java/blob/master/LICENSE.txt)のもと提供されます。