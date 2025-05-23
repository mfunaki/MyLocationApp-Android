# MyLocationApp-Android

このアプリケーションは、Androidデバイスの現在の緯度と経度を取得し、その位置情報をGoogleマップ上に表示するシンプルなサンプルアプリケーションです。

## 前提条件

開発を開始する前に、以下のソフトウェアがインストールされ、設定されていることを確認してください。

* **Android Studio:** 最新の安定版を推奨します。 (例: Iguana | 2023.2.1 以降)
* **JDK:** Android StudioにバンドルされているJDKで通常は問題ありません。
* **Google Cloud Platform (GCP) アカウント:** Google Maps APIキーを取得し、Maps SDK for Androidを有効にするために必要です。

## セットアップ手順

1.  **リポジトリのクローン:**
    まず、このリポジトリをローカルマシンにクローンします。
    ```bash
    git clone https://github.com/mfunaki/MyLocationApp-Android.git
    cd MyLocationApp-Android
    ```

2.  **Android Studioでプロジェクトを開く:**
    Android Studioを起動し、「Open an Existing Project」を選択して、クローンしたプロジェクトのルートディレクトリを開きます。

3.  **`secrets.properties` ファイルの作成 (非常に重要):**
    このプロジェクトでは、Google Maps APIキーを安全に管理するために `secrets.properties` ファイルを使用します。このファイルはセキュリティ上の理由からリポジトリには含まれていませんので、手動で作成する必要があります。

    * プロジェクトの**ルートディレクトリ**（`app` フォルダやプロジェクトレベルの `build.gradle.kts` と同じ階層）に `secrets.properties` という名前のファイルを作成してください。
    * 作成した `secrets.properties` ファイルに、以下の形式であなたのGoogle Maps APIキーを記述します。

        ```properties
        MAPS_API_KEY=ここにあなたのMaps_APIキーを貼り付けます
        ```
        `ここにあなたのMaps_APIキーを貼り付けます` の部分を、実際に取得したAPIキーに置き換えてください。

    * **APIキーの取得と設定について:**
        * APIキーは [Google Cloud Platform Console](https://console.cloud.google.com/) から取得できます。
        * GCPプロジェクトで **"Maps SDK for Android"** が有効になっていることを確認してください。
        * セキュリティ向上のため、APIキーには適切な**制限**を設定することを強く推奨します。
            * **アプリケーションの制限:** 「Androidアプリ」を選択し、パッケージ名 (`mayoct.net.mylocationapp`) と、開発に使用するマシンのデバッグ用SHA-1証明書フィンガープリント（およびリリース時にはリリース用SHA-1証明書フィンガープリント）を追加します。SHA-1フィンガープリントは、Android Studioのターミナルで `./gradlew signingReport` を実行することで確認できます。
            * **APIの制限:** 「キーを制限する」を選択し、「Maps SDK for Android」のみを許可するように設定します。

4.  **Gradleプロジェクトの同期:**
    `secrets.properties` ファイルを作成し、APIキーを設定した後、Android Studioがプロジェクトの再同期を促す場合があります。促されない場合は、ツールバーの「Sync Project with Gradle Files」ボタン（象のアイコン）をクリックするか、「File」メニュー → 「Sync Project with Gradle Files」を選択して、手動でプロジェクトを同期してください。

## アプリケーションのビルドと実行

1.  **実行構成の選択:**
    Android Studioのツールバーにある実行構成のドロップダウンメニューから `app` を選択します。

2.  **ターゲットデバイスの選択:**
    Android実機をUSBで接続するか、Android Emulatorを起動します。

3.  **アプリケーションの実行:**
    ツールバーの「Run 'app'」ボタン（緑色の再生アイコン▶️）をクリックします。

または、ターミナルから以下のGradleコマンドを使用してビルドおよびインストールすることも可能です。

```bash
# デバッグ用APKをビルド
./gradlew assembleDebug

# 接続されているデバイス/エミュレータにデバッグ用APKをインストール
./gradlew installDebug