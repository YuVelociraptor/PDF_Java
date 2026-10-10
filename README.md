# PDF_Java

Apache PDFBox を使って、PDF の結合・ページ単位の分割を行う Java 製のコマンドラインツールです。日本語テキストを描画する PDF 生成サンプルも含まれています。

## 機能

| クラス | 処理 | 出力ファイル名 |
| --- | --- | --- |
| `MergePDF` | 指定ディレクトリの PDF をファイル名の昇順で結合 | `merged_<日時>.pdf` |
| `SplitPDF` | 指定ディレクトリの各 PDF を1ページずつ分割 | `Split<日時>_<元ファイル名>_page000.pdf` など |
| `SimpleWrite` | 「こんにちは。世界」を描画する、90度回転した A4・1ページのサンプル | `out_<日時>.pdf` |
| `A4Write` | ページごとに描画位置を変える A4・30ページのサンプル | `out_<日時>.pdf` |

日時の形式は `yyyyMMddHHmmss` です。分割ファイルのページ番号は **0始まり**で、最低3桁のゼロ埋めになります（総ページ数が4桁以上の場合は、その桁数に合わせます）。

## 実行環境

- Docker で実行する場合：Docker と Docker Compose
- ローカルで実行する場合：JDK 26（Dockerfile と同じバージョン）
- ビルド：Gradle Wrapper 9.4.0（Gradle の個別インストールは不要）
- PDF ライブラリ：Apache PDFBox 3.0.5

初回のビルドには、Gradle と依存ライブラリを取得するためのインターネット接続が必要です。以下のコマンドはリポジトリのルートで実行します。

## Docker で実行する

### 1. 入出力ディレクトリを設定する

`.env` がまだない場合は、サンプルをコピーします。

```sh
cp .env.example .env
```

`.env` の3項目を、**ホスト側のディレクトリの絶対パス**に変更してください。`.env.example` の `/app/...` はサンプル値なので、実際の保存先に置き換えます。

```dotenv
PDF_OUT_DIR=/absolute/path/to/pdf/output
PDF_MERGE_INPUT_DIR=/absolute/path/to/pdf/input/merge
PDF_SPLIT_INPUT_DIR=/absolute/path/to/pdf/input/split
```

指定した3つのディレクトリを事前に作成し、結合用・分割用の入力ディレクトリに処理したい PDF を配置します。出力先は入力先と分けてください。

Compose はこれらのホスト側ディレクトリを、コンテナ内の `/app/output`、`/app/input/merge`、`/app/input/split` にそれぞれマウントします。コンテナ内の環境変数には、このコンテナ側パスが設定されます。

### 2. 起動して処理する

```sh
docker compose up -d --build
docker compose logs -f app
```

起動時に `./gradlew shadowJar` が実行されます。ログで `BUILD SUCCESSFUL` を確認したら、`Ctrl+C` でログ表示を終了し、コンテナの Bash に入ります。

```sh
docker compose exec app bash
```

コンテナ内で、必要な処理を実行します。

```sh
# 結合用ディレクトリ内の PDF を結合
merge

# 分割用ディレクトリ内の各 PDF をページ単位で分割
split
```

`merge` と `split` は、コンテナの対話型 Bash に定義されたエイリアスです。それぞれ次のコマンドに対応します。

```sh
java -cp /app/build/libs/pdf_tools.jar MergePDF
java -cp /app/build/libs/pdf_tools.jar SplitPDF
```

出力 PDF は `.env` の `PDF_OUT_DIR` で指定したホスト側ディレクトリに保存されます。シェルに入らず実行する場合は、Java コマンドを直接指定します。

```sh
docker compose exec app java -cp /app/build/libs/pdf_tools.jar MergePDF
docker compose exec app java -cp /app/build/libs/pdf_tools.jar SplitPDF
```

ソースを変更した場合は、実行前に JAR を再ビルドします。

```sh
docker compose exec app ./gradlew shadowJar
```

終了時は、コンテナのシェルから `exit` で抜け、ホスト側で停止します。

```sh
docker compose down
```

## ローカルで実行する

以下は macOS / Linux のシェルでの例です。入出力ディレクトリはリポジトリの外に作成しています。

```sh
./gradlew shadowJar

export PDF_OUT_DIR="$HOME/pdf-tools/output"
export PDF_MERGE_INPUT_DIR="$HOME/pdf-tools/input/merge"
export PDF_SPLIT_INPUT_DIR="$HOME/pdf-tools/input/split"
mkdir -p "$PDF_OUT_DIR" "$PDF_MERGE_INPUT_DIR" "$PDF_SPLIT_INPUT_DIR"
```

対応する入力ディレクトリに PDF を配置してから、必要なコマンドを実行します。

```sh
java -cp build/libs/pdf_tools.jar MergePDF
java -cp build/libs/pdf_tools.jar SplitPDF
```

依存ライブラリを含む JAR は `build/libs/pdf_tools.jar` に生成されます。実行するクラスを `java -cp` で指定してください。Windows でビルドする場合は `gradlew.bat shadowJar` を使い、環境変数は利用するシェルの書式で設定します。

### PDF 生成サンプル

`SimpleWrite` と `A4Write` では、`PDF_OUT_DIR` に加えて、日本語グリフを含む PDFBox で読み込めるフォントファイルのパスを `PDF_FONT` に設定します。

```sh
export PDF_FONT="/absolute/path/to/japanese-font.ttf"
java -cp build/libs/pdf_tools.jar SimpleWrite
java -cp build/libs/pdf_tools.jar A4Write
```

Docker Compose の標準設定にはフォントのマウントと `PDF_FONT` の設定が含まれていません。Docker で生成サンプルを動かす場合は、フォントファイルをコンテナから参照できるようにマウントし、`PDF_FONT` にコンテナ内のパスを設定してください。

## 環境変数と注意点

| 環境変数 | 用途 | 必要な処理 |
| --- | --- | --- |
| `PDF_OUT_DIR` | PDF の出力先ディレクトリ | すべて |
| `PDF_MERGE_INPUT_DIR` | 結合する PDF の入力ディレクトリ | `MergePDF` |
| `PDF_SPLIT_INPUT_DIR` | 分割する PDF の入力ディレクトリ | `SplitPDF` |
| `PDF_FONT` | 日本語描画用のフォントファイル | `SimpleWrite`、`A4Write` |

- Java プログラムは `.env` を直接読み込みません。ローカル実行時はシェルで環境変数を設定してください。
- 入出力ディレクトリは事前に作成し、入力先には読み取り権限、出力先には書き込み権限を付与してください。プログラムは出力ディレクトリを自動作成しません。
- 結合・分割は入力ディレクトリ直下の `.pdf` ファイルが対象です。拡張子の大文字・小文字は区別せず、サブディレクトリは再帰的に探索しません。
- 結合順はファイル名の文字列順です。意図した順序にするには `001_cover.pdf`、`002_body.pdf` のようにゼロ埋めした番号を付けてください。
- 入力 PDF 自体は変更しません。入力と出力に同じディレクトリを指定すると、次回実行時に過去の出力も処理対象になります。
- 出力名の日時は秒単位です。同じ出力名になる処理を同じ秒に実行すると、既存ファイルを上書きする可能性があります。生成サンプル同士も同じ命名規則を使います。
- 入力ディレクトリに PDF がない場合はメッセージを表示し、PDF を出力しません。

## ディレクトリ構成

```text
.
├── build.gradle              # 依存関係・JAR ビルド設定
├── docker-compose.yml        # コンテナ起動・入出力マウント設定
├── docker/Dockerfile         # JDK 26 と Bash エイリアス
├── .env.example              # ホスト側の入出力パスの設定例
├── gradle/wrapper/           # Gradle Wrapper
├── gradlew / gradlew.bat     # ビルド用スクリプト
└── src/main/java/
    ├── MergePDF.java
    ├── SplitPDF.java
    ├── SimpleWrite.java
    └── A4Write.java
```
