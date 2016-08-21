# mirrg.gubot.noelle

## 使用方法

実行

```
cd mirrg.gubot.noelle
java -jar ../repos/mirrg.gubot.noelle-*.*.*.jar
```

または`mirrg.gubot.noelle/run.vbs`（Windowsの場合）

- 同ディレクトリに`data.csv`が必要。
- 同ディレクトリに`faces`ディレクトリが生成される。
- 同ディレクトリに`screenshots`ディレクトリが生成される。

## 機能

- GUの画面を認識
  - 原理：
    - GUの画面の周り1pxには黒い枠線が引かれている
    - 全画面から全ての黒いピクセルの島を抽出
    - サイズがGUの画面と同じものをGUの画面がある位置と断定
- スクリーンショットをとる
- 領地選択画面からいくつかの情報を抽出する
- 指定の条件の領地が出るまで領地を飛ばす（マクロ）
