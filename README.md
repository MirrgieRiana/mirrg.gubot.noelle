﻿# mirrg.gubot.noelle

## 使用方法

実行

```
cd mirrg.gubot.noelle
java -jar ../repos/mirrg.gubot.noelle-*.*.*.jar
```

または`mirrg.gubot.noelle/run.vbs`（Windowsの場合）

## 実行に関連するファイル

- `/mirrg.gubot.noelle`: 実行ディレクトリ
  - `data.csv`: ヒロインデータファイル
  - `die.mp3`: 終了通知SE
  - `imageSelecting.png`: 領地選択画面か判断する為の画像
  - `run.vbs`: Windows用起動スクリプト
  - `faces`: ヒロインのサムネイル保存フォルダ
    - `黒.png`: 背景用画像
  - `glyphs`: 経験値抽出用グリフテクスチャ
  - `screenshots`: スクリーンショット保存フォルダ

## 機能

- GUの画面を認識
  - 原理：
    - GUの画面の周り1pxには黒い枠線が引かれている
    - 全画面から全ての黒いピクセルの島を抽出
    - サイズがGUの画面と同じものをGUの画面がある位置と断定
  - スクリーンショットをとる
  - 領地選択画面からヒロイン情報を抽出する
  - 領地選択画面から経験値情報を抽出する
    - 原理：
      - 正規表現のコンパイラ的なものを実装
      - グリフデータと比較して文字列を抽出
  - 指定の条件の領地が出るまで領地を飛ばす（マクロ）
    - 検索終了時に音を出す
    - ヒロイン名・クラス・Noelleに登録済みか否かでの判定
    - 入手経験値の範囲・経験値倍率・封印石ボーナスの有無での判定
