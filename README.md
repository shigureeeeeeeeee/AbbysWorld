# Abyss World

> 五つの深淵階層を探索し、各層の力を統合して「原初神器」の完成を目指す Minecraft Forge Mod。

Abyss World は、ボス討伐そのものではなく、深淵の各階層で得た素材を魔術・機械・装備へ発展させ、最終的なアーティファクトを鍛え上げることを目的としたコンテンツ Mod です。

現在は開発中です。既存ワールドへ導入する場合は、事前にバックアップを作成してください。

## 対応環境

| 項目 | バージョン |
| --- | --- |
| Minecraft | 1.20.1 |
| Minecraft Forge | 47.4.0（47 系） |
| TerraBlender | 3.0.1.10（3.0.1.10 以上、3.1 未満） |
| Java | 17 |
| Mod バージョン | 0.1.0 |

前提 Mod として [TerraBlender](https://www.curseforge.com/minecraft/mc-mods/terrablender) が必要です。Abyss World と TerraBlender の両方を、クライアントとサーバーへ導入してください。

## 主な要素

- **五つの深淵階層** — 忘却樹海、灰燼荒野、凍結洞窟、肉塊鉱山、虚無都市。それぞれに固有の環境効果、資源、敵、ボスが存在します。
- **侵食されたオーバーワールド** — 固有バイオーム「境界侵食地」に、枯死樹、結晶噴出口、境界碑、地表亀裂が生成されます。五種の敵対Mobが徘徊し、通常の夜より高い危険度を持ちます。
- **地上の探索拠点** — 反復攻略向け地下ダンジョン「侵食脈洞」と、境界監視者が待つ希少な大規模構造物「崩壊した境界観測院」を追加します。
- **通常世界への侵攻** — 平原、森林、タイガ、サバンナ、砂漠などの通常バイオームには、約128×128規模の「裂界侵攻城塞」が低確率で出現します。四隅の塔、兵舎、工房、中央天守、地下金庫を攻略できます。
- **探索とボス進行** — 各階層の祭壇でボスを召喚し、素材と階層核を集めて深淵覇王へ挑みます。忘却樹海には固有構造物「忘却樹海城塞」も生成されます。
- **深淵魔力** — プレイヤーが扱う魔力と深淵負荷に加え、魔力槽、導管、凝縮器、次元間中継器などで構築する設備向け魔力ネットワークがあります。
- **鉱石処理と自動化** — 粉砕・精製から四重、六重、八重、十重処理までの生産ライン、地脈採掘機、資源収穫機、アイテム／液体導管、統合保管庫を追加します。
- **グリフ魔術** — 形態・効果・拡張グリフを術式書へ組み込み、用途に合わせた術式を編纂できます。
- **装備の発展** — 階層武器、携帯魔力装備、結晶／特異点防具、交換可能な防具モジュールを追加します。
- **最終目標** — 五層統合核と深淵神核を揃え、世界律再構築炉の防衛イベントを完遂すると、複数形態を持つ原初神器が完成します。

## ゲームの始め方

1. オーバーワールドを探索し、固有バイオーム「境界侵食地」を見つけます。
2. 侵食脈洞や崩壊した境界観測院を攻略しつつ、深淵鉄と深淵結晶を集めます。
3. 深淵鉄インゴット、深淵結晶、エンダーアイから「深淵の鍵」を作成します。
4. 深淵の鍵を右クリックして最初の階層「忘却樹海」へ移動します。
5. 資源採集、機械設備、魔術、各層のボス攻略を進め、五種類の階層核を集めます。
6. 深淵覇王を倒して深淵神核を入手し、世界律再構築炉で原初神器を完成させます。

深淵の鍵は、右クリックするたびに五つの階層を順番に移動します。深淵階層内でスニークしながら右クリックするとオーバーワールドへ帰還できます。詳細な進行はゲーム内の進捗「深淵世界」も参照してください。

探索用コマンド（チートまたはOP権限が必要）:

```mcfunction
/locate biome abyssworld:boundary_scar
/locate structure abyssworld:boundary_observatory
/locate structure abyssworld:infested_vein_dungeon
/locate structure abyssworld:riftfall_citadel
```

## 導入方法

1. Minecraft 1.20.1 用の Forge 47 系をインストールします。
2. Minecraft 1.20.1 用の TerraBlender 3.0.1.10 以降（3.1 未満）を入手します。
3. TerraBlender と、ビルド済みの `abyssworld-<version>.jar` を Minecraft の `mods` フォルダーへ配置します。
4. Forge プロファイルからゲームを起動します。

マルチプレイでは、参加するクライアントとサーバーで同じバージョンの Mod を使用してください。

## 開発

### 必要なもの

- JDK 17
- Git
- Gradle Wrapper が依存関係を取得できるネットワーク環境（初回のみ）

### セットアップと実行

```bash
git clone https://github.com/shigureeeeeeeeee/abbys-craft.git
cd abbys-craft
./gradlew runClient
```

IntelliJ IDEA または Eclipse では、既存の Gradle プロジェクトとしてリポジトリを開いてください。開発用クライアントのデータは `run/` に作成されます。

よく使う Gradle タスク:

```bash
./gradlew runClient       # 開発用クライアントを起動
./gradlew runServer       # 開発用サーバーを起動
./gradlew runData         # データ生成を実行
./gradlew build           # コンパイル、検証、配布用 JAR の生成
```

ビルド成果物は `build/libs/abyssworld-<version>.jar` に出力されます。Mod ID、対応バージョン、成果物バージョンなどの設定は `gradle.properties` にあります。

## プロジェクト構成

| パス | 内容 |
| --- | --- |
| `src/main/java/com/abyssworld/` | Mod 本体、登録処理、魔術、機械、エンティティ、ワールド生成 |
| `src/main/resources/assets/abyssworld/` | テクスチャ、モデル、画面表示、日英翻訳 |
| `src/main/resources/data/abyssworld/` | レシピ、進捗、ルートテーブル、ディメンション、ワールド生成データ |
| `src/generated/resources/` | `runData` が生成するリソース |
| `art_sources/` | テクスチャ制作時の元画像 |
| `tools/` | アセット生成・加工用スクリプト |

## ライセンス

Mod 本体のメタデータ上のライセンスは **All Rights Reserved** です。Minecraft Forge など、同梱されている第三者由来の通知については [LICENSE.txt](LICENSE.txt) と [CREDITS.txt](CREDITS.txt) を参照してください。
