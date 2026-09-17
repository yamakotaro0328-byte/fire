# HanabiFestival（花火大会プラグイン）

Minecraft（Paper/Spigot 1.20.x）向けの本格的な花火大会イベントプラグインです。
ランダム乱れ打ちだけでなく、時系列で演出を組んだ「ショー」、打ち上げ地点の登録、
開始前カウントダウン演出、GUIコントロールパネルまで備えています。

## 主な機能

- **ショー(演出)システム**: `shows.yml` で複数のショーを定義可能
  - `mode: random` — 一定間隔でランダムに乱れ打ちを続ける定番モード
  - `mode: sequence` — フィナーレのように時系列で花火の量・色・形状を変化させる演出モード（リピート対応）
- **打ち上げ地点の登録**: `/hanabi setpoint` で任意の場所を「打ち上げ会場」として登録。
  登録があればそこから、なければオンラインプレイヤーの周囲から自動で打ち上げます。
- **カウントダウン演出**: 開始前にタイトル表示＋効果音でカウントダウン
- **歓声演出**: 開催中、一定間隔でサウンドを再生して盛り上げます
- **GUIコントロールパネル**: `/hanabi gui` でショー選択・停止・リロードをワンクリック操作
- **ステータス確認**: `/hanabi status` で開催中のショー名・経過時間を確認

## コマンド

| コマンド | 権限 | 説明 |
| --- | --- | --- |
| `/hanabi start [ショー名]` | `hanabi.admin` | 花火大会を開始する（省略時は `default-show`） |
| `/hanabi stop` | `hanabi.admin` | 花火大会を終了する |
| `/hanabi status` | `hanabi.admin` | 開催状況を確認する |
| `/hanabi gui` | `hanabi.admin` | GUIコントロールパネルを開く（プレイヤーのみ） |
| `/hanabi setpoint <名前>` | `hanabi.admin` | 現在地を打ち上げ地点として登録する（プレイヤーのみ） |
| `/hanabi delpoint <名前>` | `hanabi.admin` | 打ち上げ地点を削除する |
| `/hanabi points` | `hanabi.admin` | 登録済みの打ち上げ地点一覧を表示する |
| `/hanabi reload` | `hanabi.admin` | すべての設定ファイルを再読み込みする |

エイリアス: `/花火大会`, `/fireworksfestival`　（サブコマンド・ショー名・地点名のタブ補完対応）

## 設定ファイル

- `config.yml`: カウントダウン秒数、既定ショー名、既定の花火色・形状、歓声間隔など全体設定
- `shows.yml`: ショー定義（`random` / `finale` / `kickoff` をサンプル同梱）
- `messages.yml`: 全メッセージ（`&`で色コード指定可、`{show}` `{seconds}` `{point}` 等のプレースホルダー対応）
- `points.yml`: 登録した打ち上げ地点データ（コマンドから自動生成・保存）

### ショー定義の例（`shows.yml`）

```yaml
shows:
  finale:
    display-name: "&6&lグランドフィナーレ"
    mode: sequence
    repeat: 3
    repeat-delay-ticks: 40
    steps:
      - delay-ticks: 0
        count: 2
        power: 2
        radius: 5
        colors: ["FF0000", "FFD700"]
        types: ["BALL_LARGE"]
      - delay-ticks: 10
        count: 4
        colors: ["00FFFF", "FFFFFF"]
        types: ["BURST", "STAR"]
```

## ビルド方法

```
mvn package
```

`target/HanabiFestival-1.0.0.jar` が生成されるので、サーバーの `plugins` フォルダに配置してください。
