(ns withings-client.core
  (:require
   [ajax.core :refer [GET POST]]
   [cljs.math :refer  [pow]]
   [clojure.string :as string]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reitit.core :as reitit]
   [withings-client.ajax :as ajax])
  (:import
   goog.History))

(def ^:private version "0.12.2")

;; FIXME: better way?
(def redirect-uri
  (try
    js/redirectUrl
    (catch js/Error _ "https://wc.kohhoh.jp/callback")))

(defonce session
  (r/atom {:page :home
           :home {:name   nil
                  :cid    nil
                  :secret nil
                  :belong nil
                  :email  nil
                  :uri    nil
                  ;; こっちじゃないか。
                  :line_id  nil
                  :bot_name nil}
           :users {}
           :measures {}
           :data {:lastupdate "2022-09-01"
                  :startdate  "2022-01-01 00:00:00"
                  :enddate    "2022-12-31 23:59:59"
                  :results    nil}
           :user {}})) ;; user-page

;; to avoid reload
(declare fetch-users!)

;; ---------------------------------------------------------
;; navbar
(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page (:page @session)) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}}
       "Withings-Client"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span] [:span] [:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "#/" "Home" :home]
       [nav-link "#/data" "Data" :data]
       [nav-link "#/about" "About" :about]
       [nav-link "/logout" "Logout"]
       [nav-link "https://developer.withings.com/api-reference" "API"]]]]))

;; ----------------------------------------------------------
;; about-page
(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]
   [:p version]])

;; ----------------------------------------------------------
;; user-page
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
  [:button
   {:class "button is-primary is-small"
    :on-click
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
  [:button
   {:class "button is-danger is-small"
    :on-click
    (fn []
      (and (js/confirm "are you OK?")
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

;; ----------------------------------------------------------
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
   [:button {:class "button is-primary is-small"
             :on-click
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
   [:p "(*)は Withings からのダウンロードに必須。bot_name もいるか？"]
   [:p "create ボタンの後、下に現れるリンクをクリックすると
        acccess トークン、refresh トークンの取得に取り掛かる。
        ページが切り替わるのに 5 秒くらいかかる。非同期通信でスピードアップ予定。"]])

(defn link-component []
  [:div
   [:p "create してからクリックで登録 → "
    [:a {:href (-> @session :home :uri)}
     (-> @session :home :name)]]])

(defn refresh-button
  [user]
  [:button
   {:class "button is-primary is-small"
    :on-click
    (fn [_] (POST (str "/api/token/" (:id user) "/refresh")
              {:format :json
               :handler (fn [_]
                          (fetch-users!)
                          (js/alert "リフレッシュ完了。"))
               :error-handler #(js/alert "失敗。")}))}
   "refresh"])

(defn edit-button
  [user]
  [:button
   {:class "button is-primary is-small"
    :on-click #(swap! session assoc :user user :page :user)}
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

(defn users-component []
  [:div
   [:h2 "users"]
   [:p "アクセストークンは 10800 秒（3時間）で切れます。"]
   (doall
    (for [user (-> @session :users)]
      [:div {:class "columns" :key (:id user)}
       (for [[key e] (map-indexed vector
                                  [(if (:valid user) "y" "n")
                                   (:id user)
                                   (:name user)
                                   (:belong user)
                                   (tm (:updated_at user))
                                   [refresh-button user]
                                   [edit-button user]])]
         (users-component-aux key e))]))])

(defn home-page []
  [:section.section>div.container>div.content
   (new-component)
   [:br]
   (link-component)
   [:hr]
   (users-component)
   [:hr]
   version])

;; ------------------------------------------------------------
;; data-page
;;
;; misc functions
(defn ts->date
  "after converting to milli, doing jobs."
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
             (fn [e] (swap! session assoc-in [:data :id]
                            (-> e .-target .-value)))}
    (for [user (cons {:id 0 :name "選んでください"}
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
               (swap! session assoc-in [:data :meastype]
                      (-> e .-target .-value)))}
    (for [mea (cons {:id 0 :description "選んでください"}
                    (-> @session :measures))]
      [:option {:key (str "m" (:id mea)) :value (:value mea)}
       (:description mea)])]])

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
  []
  [:div
   [:button
    {:class "button is-primary is-small"
     :on-click
     #(POST "/api/meas"
        {:format :json
         :params {:id         (-> @session :data :id)
                  :meastype   (-> @session :data :meastype)
                  :startdate  (-> @session :data :startdate)
                  :enddate    (-> @session :data :enddate)
                  :lastupdate (-> @session :data :lastupdate)}
         :handler
         (fn [res]
           (swap! session assoc-in [:data :results] res))
         :error-handler
         (fn [e]
           (js/alert (-> e :response :body)))})}
    "fetch"]])

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
   [fetch-button]])

;; params has `created` param. which should be displayed?
(defn output-one
  [n {:keys [date measures]}]
  [:div {:key n}
   (str (ts->date date) ", " (value->float 1 measures))])

(defn user-name
  [id]
  (->> @session
       :users
       (filter #(= id (:id %)))
       first
       :name))

(defn measure-name
  [id]
  (->> @session
       :measures
       (filter #(= id (:id %)))
       first
       :description))

(defn output-component
  []
  [:div
   [:h3 "fetched ("
    (-> @session :data :id js/parseInt user-name)
    ", "
    (-> @session :data :meastype js/parseInt measure-name)
    ")"]
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

;; ------------------------------------------------------------
(def pages
  {:home  #'home-page
   :about #'about-page
   :user  #'user-page
   :data  #'data-page})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes
(def router
  (reitit/router
   [["/"      :home]
    ["/about" :about]
    ["/user"  :user]
    ["/data"  :data]]))

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

(defn fetch-measures! []
  (GET "/api/meas" {:handler #(swap! session assoc :measures %)}))

(defn ^:dev/after-load mount-components []
  (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-users!)
  (fetch-measures!)
  (hook-browser-navigation!)
  (mount-components))
