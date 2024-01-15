# raas-be-ktor-sample

Ktor向けBackendソースのサンプルです

## How to setup
### 1.1 githubのuser/tokenを環境変数に登録する
SuTech社提供のraas-client-ktorをGitHub.Packageから取得出来るようにする
(SuTech社による権限付与が必要)
```
export RAAS_GITHUB_USER=XXXXXXX
export RAAS_GITHUB_TOKEN=XXXXXXX
```
### 1.2 gradleでbuildする
```
./gradlew build
```
### 1.3 application.yamlを準備する
（SuTech社より取得した値をXXXX部分に記載）
```
ktor:
  deployment:
    port: 8080
    host: 0.0.0.0
  application:
    modules: [com.example.ApplicationKt.module]

raas:
  application: XXXXX
  landscape: dev
  token: XXXXX

```
### 1.4 gradleでrunする
```
./gradlew run
```

## 概要
feのsampleをSuTech社より取得して結合する
### 2.1 FEを初期化する為のsession作成を行う
/raas/report/session
/raas/datatraveler/session
にアクセスがあった際に該当の処理が起動する

### 2.2 帳票作成結果（PDF/JSON/CSV）を取得する
/raas/report/result/{targetId}
にアクセスがあった際に該当の処理が起動する

## 組込方法
### 3.1 raas-client-ktorを利用するための定義をGradleに行う
build.gradle.ktsの以下の２部分を参考に行う

GitHub.Packagesに接続（build.gradle.ktsのrepositories部分に追加）
```
maven {
    url = uri("https://maven.pkg.github.com/SuTech-JP/raas-client-ktor")
    credentials {
        username = System.getenv("RAAS_GITHUB_USER")
        password = System.getenv("RAAS_GITHUB_TOKEN")
    }
}
```
※上記例の場合には環境変数を利用して接続する事になる（CI/CDで利用しやすい形に変更する）

Dependencyにraas-client-ktorを追加（build.gradle.ktsのdependencies部分に追加）
```
implementation("jp.co.sutech:raas-client-ktor:0.0.17-SNAPSHOT")
```

### 3.2 FE用のsession関数を作成する
2.1と同等の処理を作成する。

### 3.3 データ連携処理を作成する（DataImportLogIdを保存する）
取り込みを実行した後にDataImportLogIdを保存する
（tenant , sub も一緒に保存することを推奨する）

### 3.4 データ連携処理を作成する（作成処理が終わったDataImportLogIdの結果を取得する）
3.3のデータを元にBEにてraasをポーリングして、処理が終わった際には2.2と同等の処理を作成する。
（WebHookによる通知も可能である）
