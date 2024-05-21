(ns withings-client.core
  (:require
   [ajax.core :refer [GET POST]]
   [cljs.math :refer  [pow]]
   [clojure.string :as string]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   ;; [java-time.api :as jt] ; no in CLJS.
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reitit.core :as reitit]
   [withings-client.ajax :as ajax])
  (:import
   goog.History))

(def ^:private version "v1.29.606")

(def redirect-uri "https://wc.kohhoh.jp/callback")

(comment
  ;;(jt/instant) ; no in CLJS.
  (js/Date)
  :rcf)

(defonce session
  (r/atom {:page :home
           :home {:name   nil
                  :cid    nil
                  :secret nil
                  :belong nil
                  :email  nil
                  :uri    nil
                  :line_id  nil
                  :bot_name nil}
           :users {}
           :measures {}
           :data {:lastupdate ""
                  :startdate  "2024-01-01 00:00:00"
                  :enddate    "2024-12-31 23:59:59"
                  :results    nil}
           :refresh "???"
           :refresh-all "スピードアップしました"
           :user {}})) ;; user-page

;; to avoid reload
(declare fetch-users!)

;; ---------------------------------------------------------
;; navbar
(defonce expanded? (r/atom false))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page (:page @session)) "is-active")}
   title])

(defn navbar []
  [:nav.navbar.is-info>div.container
   [:div.navbar-brand
    [:a.navbar-item {:href "" :style {:font-weight :bold}}
     "Withings-Client"]
    [:span.navbar-burger.burger
     {:data-target :nav-menu
      :on-click #(swap! expanded? not)
      :class (when @expanded? :is-active)}
     [:span] [:span] [:span]]]
   [:div#nav-menu.navbar-menu
    {:class (when @expanded? :is-active)}
    [:div.navbar-start
     ;; [nav-link "" "Home" :home]
     [nav-link "#/new" "New" :new]
     [nav-link "#/users" "Users" :users]
     [nav-link "#/data" "Data" :data]
     [nav-link "#/about" "About" :about]
     [nav-link "/logout" "Logout"]
     [nav-link "https://developer.withings.com/api-reference" "API"]]]])

;; ----------------------------------------------------------
;; about-page
(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]
   [:p version]])

;; ----------------------------------------------------------
;; user-page, called from edit button.
(defn user-component
  []
  [:div
   [:h3 (-> @session :user :name)]
   [:p "valid は 0/1 で変更"]
   (doall (for [[key _] (dissoc (-> @session :user)
                                :id :userid :created_at :updated_at)]
            [:p {:key key} (symbol key)
             [:br]
             [:input
              {:class "input"
               :value (get-in @session [:user key])
               :on-change
               #(swap! session
                       assoc-in [:user key] (-> % .-target .-value))}]]))])

(defn update-button
  []
  [:button.button.is-primary.is-small
   {:on-click
    (fn [^js/Event e]
      ;; (js/alert (str (:user @session)))
      (POST (str "/api/user/" (get-in @session [:user :id]))
        {:params (:user @session)
         :handler (fn [_]
                    (fetch-users!)
                    (swap! session assoc :page :home))
         :error-handler (fn [] (js/alert (.getMessage e)))}))}
   "update"])

(defn delete-button
  []
  [:button.button.is-danger.is-small
   {:on-click
    (fn []
      (and (js/confirm (str "delete " (-> @session :user :name) " ?"))
           (POST (str "/api/user/" (-> @session :user :id) "/delete")
             {:handler (fn [_]
                         (fetch-users!)
                         (swap! session assoc :page :home))
              :error-handler
              (fn [^js/Event e] (js/alert (.getMessage e)))})))}
   "delete"])

(defn user-page
  []
  [:section.section>div.container>div.content
   [user-component]
   [:br]
   [update-button]
   [:br]
   [:br]
   [delete-button]])

; ----------------------------------------------------------
;; home page
;;
(def scope "user.metrics,user.activity,user.info")
(def authorize2-uri "https://account.withings.com/oauth2_user/authorize2")
(def base
  (str authorize2-uri
       "?response_type=code&redirect_uri=" redirect-uri "&"
       "scope=" scope "&"))

(defn create-url
  []
  (str base
       "client_id=" (-> @session :home :cid)
       "&state=" (-> @session :home :name)))

(defn create-user!
  ":name, :cid, :secret are required field.
   FIXME: lack validations."
  [params]
  (POST "/api/user"
    {:format :json
     :params params
     :handler (fn [_]
                (js/alert (str "saved" params))
                (fetch-users!))
     :error-handler
     (fn [e] (js/alert (get-in e [:response :errors :server-error])))}))

(defn create-button
  []
  [:div {:class "field"}
   [:button.button.is-primary.is-small
    {:on-click
     #(let [params (select-keys
                    (-> @session :home)
                    [:name :cid :secret :belong :email])]
        (create-user! params)
        (swap! session assoc-in [:home :uri] (create-url)))}
    "create"]])

(defn sub-field
  [key label]
  [:div {:key key}
   [:div [:label {:class "label"} label]]
   [:div {:class "field"}
    [:input {:class "input"
             :value (key (-> @session :home))
             :on-change
             #(swap! session
                     assoc-in
                     [:home key]
                     (-> % .-target .-value))}]]])

(defn new-component []
  [:div
   [:h3 "new"]
   (doall
    (for [[key label] {:name     "name (*)"
                       :cid      "cid (*)"
                       :secret   "secret (*)"
                       :belong   "belong"
                       :email    "email"
                       :line_id  "line_id"
                       :bot_name "bot_name"}]
      (sub-field key label)))
   [:br]
   [create-button]
   [:p "(*)は Withings からのダウンロードに必須な情報です。"]
   [:p "(*)を埋めてから create で データベースに access/refresh トークン抜きのエントリーを作る。"]
   [:p "create ボタン押した後、この下に現れる氏名のリンクが
        Withings から acccess/refresh トークン を取ってきてデータベースに追加します。
        リンクをクリックして Withings のページが表示されるまで 5 秒くらいかかる。"]])

(defn link-component []
  [:div
   [:p "create してからクリックで登録 → "
    [:a {:href (-> @session :home :uri)}
     (-> @session :home :name)]]])

(defn refresh-button
  [id] ;; user
  [:button.button.is-primary.is-small
   {:on-click
    (fn [_] (POST (str "/api/token/" id "/refresh")
              {:format :json
               :handler (fn [_]
                          (fetch-users!)
                          (swap! session assoc :refresh "OK")
                          #_(js/alert "リフレッシュ完了。"))
               :error-handler #(js/alert "失敗。")}))}
   "refresh"])

(defn edit-button
  [user]
  [:button.button.is-primary.is-small
   {:on-click #(swap! session assoc :user user :page :user)}
   "edit"])

;; used in users-component only.
(defn tm
  "returns strung yyyy-mm-dd hh:mm from tagged value tv"
  [^js/LocalDateTime tv]
  (let [s (.-rep tv)]
    (str (subs s 0 10) " " (subs s 11 16))))

(defn users-component-aux
  [key e]
  [:div {:key key :class "column"} e])

;; 2023-06-25
(defn shorten [n s]
  (if (empty? s)
    [:span {:class "red"} "empty"]
    (str (subs s 0 n) "...")))

(defn refresh-all-button
  []
  [:button.button.is-primary.is-small
   {:on-click
    (fn [_]
      (swap! session assoc :refresh-all "wait...")
      (POST (str "/api/tokens/refresh-all")
        {:format :json
         :handler (fn [_]
                    (fetch-users!)
                    #_(js/alert "リフレッシュ完了。")
                    (swap! session assoc :refresh-all "done."))
         :error-handler #(js/alert "失敗。")}))}
   "refresh-all"])

(defn users-component []
  [:div
   [:h2 "users"]
   [:p "アクセストークンは 10800 秒（3時間）で切れるとなってるが、
        もっと短い時間で切れてるんじゃ？"]
   [:div.columns
    [:div [refresh-all-button]] [:div (-> @session :refresh-all)]]
   [:div {:class "columns"}
    (for [col ["valid" "id" "name" "userid" #_"belong" "cid" "access" "update" #_"" ""]]
      [:div {:class "column has-text-weight-bold"} col])]
   (doall
    (for [user (-> @session :users)]
      [:div {:class "columns" :key (:id user)}
       (for [[key e] (map-indexed vector
                                  [(if (:valid user) "y" "n")
                                   (:id user)
                                   (:name user)
                                   (:userid user)
                                   #_(:belong user)
                                   (shorten 6 (:cid user))
                                   (shorten 6 (:access user))
                                   (tm (:updated_at user))
                                   ;;
                                   #_[refresh-button (:id user)]
                                   [edit-button user]])]
         (users-component-aux key e))]))])

(defn home-page []
  [:section.section>div.container>div.content
   [new-component]
   [:br]
   [link-component]
   [:hr]
   [users-component]
   [:hr]
   version])

;; ------------------------------------------------------------
;; data-page
;;
(defn ts->date
  "After converting to milli, return with 24-hour time format."
  [ts]
  (-> (* 1000 ts)
      js/Date.
      (.toLocaleString "en-GB"))) ;; for 24-hour time format

(defn value->float
  "withings-value -> float"
  [digits [{:keys [value unit]}]]
  (-> (/ value (pow 10 (- unit)))
      (.toFixed digits)))

(defn select-id
  []
  [:div
   [:select {:name "id"
             :on-change
             (fn [e]
               (swap! session assoc-in [:data :results] nil)
               (swap! session assoc-in [:data :id]
                      (-> e .-target .-value)))}
    (for [user (cons {:id 0 :name "氏名"}
                     (->> @session
                          :users
                          (filter :valid)))]
      [:option {:key (:id user) :value (:id user)} (:name user)])]])

(defn select-meatype
  []
  [:div
   [:select {:name "meastype"
             :on-change
             (fn [e]
               ;; (js/alert (-> e .-target .-value))
               (swap! session assoc-in [:data :results] nil)
               (swap! session assoc-in [:data :meastype]
                      (-> e .-target .-value)))}
    (for [item (cons {:id 0 :description "測定項目"} (:measures @session))]
      [:option {:key (str "m" (:id item)) :value (:value item)}
       (str (:value item) " " (:description item))])]])

(defn input-startdate-enddate
  []
  [:div
   [:p [:b "start ~ end "]
    [:input {:name "start"
             :value (-> @session :data :startdate)
             :on-change #(swap! session
                                assoc-in
                                [:data :startdate]
                                (-> % .-target .-value))}]
    " ~ "
    [:input {:name "end"
             :value (-> @session :data :enddate)
             :on-change #(swap! session
                                assoc-in
                                [:data :enddate]
                                (-> % .-target .-value))}]
    " hh:mm:ss を省略すると 00:00:00 と解釈します。"]])

(defn input-lastupdate
  []
  [:div
   [:p [:b "lastupdate "]
    [:input {:value (-> @session :data :lastupdate)
             :on-change #(swap! session
                                assoc-in
                                [:data :lastupdate]
                                (-> % .-target .-value))}]
    " ~ "
    [:b "now"]
    " 日時を記入するとこちらを優先する。カラだと start ~ end を取る。"]])

(defn fetch-button
  [id]
  [:div
   [:button.button.is-primary.is-small
    {:on-click
     (fn [_]
       ;; (POST (str "/api/token/" (-> @session :data :id) "/refresh")
       (POST "/api/meas"
         {:format :json
          :params {:id         id ;; (-> @session :data :id)
                   :meastype   (-> @session :data :meastype)
                   :startdate  (-> @session :data :startdate)
                   :enddate    (-> @session :data :enddate)
                   :lastupdate (-> @session :data :lastupdate)}
          :handler
          (fn [res]
            (swap! session assoc-in [:data :results] res))
          ;; error does not happen
          :error-handler
          (fn [_] (js/alert "/api/meas error"))}))}
    "fetch"]])

(defn user-name
  [id]
  (->> @session
       :users
       (filter #(= id (:id %)))
       first
       :name))

(defn measure-name
  [n]
  (->> @session
       :measures
       (filter #(= n (:value %))) ;; fixed 2022-12-29
       first
       :description))

(defn input-component
  "id, meatype, startdate, enddate are required to work.
   date must be in  `yyyy-MM-dd hh:mm:ss` format.
   FIXME: validation."
  []
  [:div
   [:h3 "Data"]
   [select-id]
   [select-meatype]
   [input-startdate-enddate]
   [:p "or"]
   [input-lastupdate]
   [:br]
   [:div.columns
    [:div.column.is-1 [fetch-button (-> @session :data :id)]]
    [:div.column.is-1 [refresh-button (-> @session :data :id)]]
    [:div#refresh.column.is-1 (-> @session :refresh)]]
   #_[:div.columns
      [:div.column.is-one-quarter
       (-> @session :data :id js/parseInt user-name)
       ", "
       (-> @session :data :meastype js/parseInt measure-name)]
      [:div.column [fetch-button]]]])

;; params has `created` param. which should be displayed?
(defn output-one
  [n {:keys [date measures]}]
  [:div {:key n}
   (str (ts->date date) ", " (value->float 1 measures))])

(defn output-component
  []
  [:div
   [:h3 (-> @session :data :id js/parseInt user-name)
    ", "
    (-> @session :data :meastype js/parseInt measure-name)]
   (if (seq (-> @session :data :results))
     (for [[n data] (map-indexed
                     vector
                     (-> @session :data :results))]
       (output-one n data))
     [:p "no data"])])

(defn data-page []
  [:section.section>div.container>div.content
   (input-component)
   [:hr]
   (output-component)
   [:hr]
   version])

(defn new-page []
  [:section.section>div.container>div.content
   [new-component]
   [:hr]
   version])

(defn users-page []
  [:section.section>div.container>div.content
   [users-component]
   [:hr]
   version])
;; ------------------------------------------------------------
(def pages
  {:home  #'home-page
   :about #'about-page
   :user  #'user-page
   :data  #'data-page
   ;;
   :new   #'new-page
   :users #'users-page})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes
(def router
  (reitit/router
   [["/"      :home]
    ["/about" :about]
    ["/user"  :user]
    ["/data"  :data]
    ;;
    ["/new"   :new]
    ["/users" :users]]))

(defn match-route [uri]
  (->> (or (not-empty (string/replace uri #"^.*#" "")) "/")
       (reitit/match-by-path router)
       :data
       :name))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     HistoryEventType/NAVIGATE
     (fn [^js/Event.token event]
       (swap! session assoc :page (match-route (.-token event)))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-users! []
  (GET "/api/users" {:handler #(swap! session assoc :users %)}))

(defn fetch-measures!
  "Get English/Japanese descriptions of measure types.
   Many Japanese descriptions are empty."
  []
  (GET "/api/measures" {:handler #(swap! session assoc :measures %)}))

(defn ^:dev/after-load mount-components []
  (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  (rdom/render [#'page]   (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-users!)
  (fetch-measures!)
  (hook-browser-navigation!)
  (mount-components))
