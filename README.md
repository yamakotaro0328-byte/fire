# HanabiFestival（花火大会プラグイン）

Minecraft（Paper/Spigot 1.20.x）向けの花火大会イベントプラグインです。
コマンド一つで、オンラインの全プレイヤーの周囲に花火を連続で打ち上げるお祭りイベントを開催できます。

## コマンド

| コマンド | 権限 | 説明 |
| --- | --- | --- |
| `/hanabi start` | `hanabi.admin` | 花火大会を開始する |
| `/hanabi stop` | `hanabi.admin` | 花火大会を終了する |
| `/hanabi reload` | `hanabi.admin` | `config.yml` を再読み込みする |

エイリアス: `/花火大会`, `/fireworksfestival`

## 設定 (`config.yml`)

- `interval-ticks`: 花火を打ち上げる間隔（tick）
- `fireworks-per-launch`: 1回あたりにプレイヤーごとに打ち上げる花火の数
- `radius`: プレイヤー周囲の打ち上げ半径（ブロック）
- `power`: 花火の上昇力（0〜3）
- `duration-seconds`: 自動終了までの秒数（0で無制限、`/hanabi stop`まで継続）
- `colors` / `types`: 花火の色・形状のバリエーション
- `flicker` / `trail`: きらめき・尾引き効果の有無
- `messages`: 各種メッセージ（`&`で色コード指定可）

## ビルド方法

```
mvn package
```

`target/HanabiFestival-1.0.0.jar` が生成されるので、サーバーの `plugins` フォルダに配置してください。
