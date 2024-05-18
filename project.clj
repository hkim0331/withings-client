(defproject withings-client "v1.25.579"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[ch.qos.logback/logback-classic "1.4.11"]
                 [cljs-ajax "0.8.4"]
                 [clojure.java-time "1.3.0"]
                 [com.cognitect/transit-clj "1.0.333"]
                 [com.cognitect/transit-cljs "0.8.280"]
                 [com.google.javascript/closure-compiler-unshaded "v20230802"]
                 [com.google.protobuf/protobuf-java "3.24.3"]
                 [conman "0.9.6"]
                 [cprop "0.1.19"]
                 [expound "0.9.0"]
                 [funcool/struct "1.4.0"]
                 [json-html "0.4.7"]
                 [luminus-migrations "0.7.5"]
                 [luminus-transit "0.1.6"]
                 [luminus-undertow "0.1.18"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.11.5"]
                 [metosin/muuntaja "0.6.8"]
                 [metosin/reitit "0.6.0"]
                 [metosin/ring-http-response "0.9.3"]
                 [mount "0.1.17"]
                 [mysql/mysql-connector-java "8.0.33"]
                 [nrepl "1.0.0"]
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.11.121" :scope "provided"]
                 [org.clojure/core.async "1.6.681"]
                 [org.clojure/tools.cli "1.0.219"]
                 [org.clojure/tools.logging "1.2.4"]
                 [org.webjars.npm/bulma "0.9.4"]
                 [org.webjars.npm/material-icons "1.13.2"]
                 [org.webjars/webjars-locator "0.47"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [reagent "1.2.0"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.10.0"]
                 [ring/ring-defaults "0.4.0"]
                 [selmer "1.12.59"]
                 [thheller/shadow-cljs "2.25.5" :scope "provided"]
                 ;;
                 [buddy/buddy-auth "3.0.323"]
                 [buddy/buddy-core "1.11.423"]
                 [buddy/buddy-hashers "2.0.167"]
                 [buddy/buddy-sign "3.5.351"]
                 [cheshire/cheshire "5.12.0"]
                 [hato/hato "0.9.0"]]


  :min-lein-version "2.0.0"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot withings-client.core

  :plugins []
  :clean-targets ^{:protect false}
  [:target-path "target/cljsbuild"]


  :profiles
  {:uberjar {:omit-source true

             :prep-tasks ["compile" ["run" "-m" "shadow.cljs.devtools.cli" "release" "app"]]
             :aot :all
             :uberjar-name "withings-client.jar"
             :source-paths ["env/prod/clj"  "env/prod/cljs"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[binaryage/devtools "1.0.7"]
                                 [cider/piggieback "0.5.3"]
                                 [org.clojure/tools.namespace "1.4.4"]
                                 [pjstadig/humane-test-output "0.11.0"]
                                 [prone "2021-04-23"]
                                 [ring/ring-devel "1.10.0"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.25.0"]
                                 [jonase/eastwood "1.4.0"]
                                 [cider/cider-nrepl "0.38.1"]]
                  :source-paths ["env/dev/clj"  "env/dev/cljs" "test/cljs"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :timeout 120000}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}})
