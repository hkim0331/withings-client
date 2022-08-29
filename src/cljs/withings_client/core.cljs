(ns withings-client.core
  (:require
   [ajax.core :refer [GET POST]]
   ;; [cljs.core.async :refer [<!]]
   [clojure.string :as string]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   ;; [markdown.core :refer [md->html]]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reitit.core :as reitit]
   [withings-client.ajax :as ajax])
  #_(:require-macros
     [cljs.core.async.macros :refer [go]])
  (:import
   goog.History))

(def ^:private version "0.6.3-SNAPSHOT")

(def redirect-uri js/redirectUrl)
;; (def redirect-uri "https://wc.melt.kyutech.ac.jp/callback")


(defonce session (r/atom {:page :home
                          :name nil
                          :cid nil
                          :secret nil
                          :belong nil
                          :email nil
                          :demo "data"}))

;; should be a member of session atom?
(defonce users (r/atom {}))
(defonce measures (r/atom {}))

;; --------------------------------------
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
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "Withings-Client"]
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

;; -------------------------
;; about page
(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]
   ;; test csrf-token
   ;; [:p js/csrfToken]
   [:p version]])

;; -------------------------
;; user page
(defn demo
  [user]
  [:div
   [:button
    {:on-click
     #(POST "/api/meas"
        {:format :json
         :headers
         {"Accept" "application/transit+json"
          "x-csrf-token" js/csrfToken}
         :params {:id        (:id user)
                  :meastype  1
                  :startdate "2022-01-01 00:00:00"
                  :enddate   "2022-10-01 00:00:00"}
         :handler (fn [res] (swap! session assoc :demo res))
         :error-handler (fn [e] (js/alert (str  "error demo" e)))})}
    "demo"]
   " 2022-01-01 から 2022-09-30 までの体重データを取得、表示します。"])

(defn user-page
  []
  (let [user (:edit @session)]
    [:section.section>div.container>div.content
     [:h2 (:name user)]
     [:h3 "under construction"]
     [demo user]
     [:div {:id "demo"} (str (-> @session :demo :measuregrps))]
     (for [[key value] user]
       [:p {:key key} (symbol key) ": " (str value) [:br]
        [:input
         {:on-key-up #(.log js/console (.-key %))}]]) ;; Enter でなんとかする。
     [:div
      [:button
       {:class "button is-danger is-small"
        :on-click
        (fn []
          (POST (str "/api/user/" (:id user) "/delete")
            {:handler #(swap! session assoc :page :home)
             :error-handler (fn [^js/Event e] (js/alert (.getMessage e)))}))}
       "delete"]]]))

;; -------------------------
;; home page
(def scope "user.metrics,user.activity,user.info")
(def authorize2-uri "https://account.withings.com/oauth2_user/authorize2")
(def base
  (str authorize2-uri
       "?response_type=code&redirect_uri=" redirect-uri "&"
       "scope=" scope "&"))

(defn create-url
  []
  (str base "client_id=" (:cid @session) "&state=" (:name @session)))

(defn create-user!
  ":name, :cid, :secret は必須フィールド。元バージョンはチェックが抜けている。"
  [params]
  (POST "/api/user"
    {:format :json
     :headers
     {"Accept" "application/transit+json"
      "x-csrf-token" js/csrfToken}
     :params params
     :handler (fn [_] (js/alert (str "saved" params)))
     :error-handler (fn [e] (js/alert (str  "error /api/user" e)))}))

(defn new-component []
  [:div
   [:h2 "new"]
   [:div [:label {:class "label"} "name (*)"]]
   [:div {:class "field"}
    [:input {:value (:name @session)
             :on-change #(swap! session
                                assoc
                                :name
                                (-> % .-target .-value))}]]
   [:div [:label {:class "label"} "cid (*)"]]
   [:div {:class "field"}
    [:input {:on-change #(swap! session
                                assoc
                                :cid
                                (-> % .-target .-value))}]]
   [:div [:label {:class "label"} "secret (*)"]]
   [:div {:class "field"}
    [:input {:on-change #(swap! session
                                assoc
                                :secret
                                (-> % .-target .-value))}]]
   [:div [:label {:class "label"} "belong"]]
   [:div {:class "field"}
    [:input {:on-change #(swap! session
                                assoc
                                :belong
                                (-> % .-target .-value))}]]
   [:div [:label {:class "label"} "email"]]
   [:div {:class "field"}
    [:input {:on-change #(swap! session
                                assoc
                                :email
                                (-> % .-target .-value))}]]
   [:div {:class "field"}
    [:button {:class "button is-primary is-small"
              :on-click #(let [params (select-keys
                                       @session
                                       [:name :cid :secret :belong :email])]
                           (create-user! params)
                           (swap! session
                                  assoc
                                  :uri
                                  (create-url)))}
     "create"]]])

(defn link-component []
  [:div
   [:p "(*)は必須フィールド。belong, email はカラでもよい。" [:br]
    "create ボタンの後、下に現れるリンクをクリックすると"
    "acccess トークン、refresh トークンの取得に取り掛かる。"
    "ページが切り替わるのに 5 秒くらいかかる。非同期通信でスピードアップ予定。"]
   [:p "クリックで登録 → " [:a {:href (:uri @session)} (:name @session)]]])

(defn tm
  "returns strung yyyy-mm-dd hh:mm from tagged value rv"
  [^js/LocalDateTime tv]
  (let [s (.-rep tv)]
    (str (subs s 0 10) " " (subs s 11 16))))

;; can not (sort-by :update_at @user)
;; since tagged value (:update_at @user)?
;; use async?
(defn users-component []
  [:div
   [:h2 "users"]
   [:p "アクセストークンは 10800 秒（3時間）で切れます。"]
   (for [user @users]
     [:div {:class "columns" :key (:id user)}
      [:div {:class "column"} (if (:valid user) "y" "n")]
      [:div {:class "column"} (:id user)]
      [:div {:class "column"} (:name user)]
      [:div {:class "column"} (:belong user)]
      [:div {:class "column"} (:email user)]
      [:div {:class "column"} (tm (:updated_at user))]
      [:div {:class "column"}
       [:button
        {:class "button is-primary is-small"
         :on-click
         (fn [_] (POST "/api/token/refresh"
                   {:format :json
                    :headers
                    {"Accept" "application/transit+json"
                     "x-csrf-token" js/csrfToken}
                    :params user
                    :handler       #(js/alert "リフレッシュ完了。")
                    :error-handler #(js/alert "失敗")}))}
        "refresh"]]
      [:div {:class "column"}
       [:button
        {:class "button is-primary is-small"
         :on-click #(swap! session assoc :page :user :edit user)}
        "edit"]]])])

(defn home-page []
  [:section.section>div.container>div.content
   (new-component)
   [:br]
   (link-component)
   [:hr]
   (users-component)
   [:hr]
   version])

;; ------------------
;; data-page
(defn input-component
  "form. must pass id meatype startdate enddate.
   date format is yyyy-MM-dd hh:mm:ss"
  []
  (let [id        (r/atom (:id (first @users)))
        meastype  (r/atom (:id (first @measures)))
        startdate (r/atom "2022-01-01 00:00:00")
        enddate   (r/atom "2023-01-01 00:00:00")]
    [:div [:h3 "Data"]
     [:select {:name "id"
               :on-change (fn [e] (reset! id (-> e .-target .-value)))}
      (for [user @users]
        [:option {:key (:id user) :value (:id user)} (:name user)])]
     [:div
      [:select {:name "meastype"
                :on-change (fn [e] (reset! meastype (-> e .-target .-value)))}
       (for [mea @measures]
         [:option {:key (str "m" (:id mea)) :value (:value mea)}
          (:description mea)])]]
     [:div
      [:p [:b "start "]
       [:input {:name "start"
                :placeholder "yyyy-MM-dd hh:mm:ss"
                :on-key-up #(reset! startdate (-> % .-target .-value))}]]
      [:p [:b "end "]
       [:input {:name "end"
                :placeholder "yyyy-MM-dd hh:mm:ss"
                :on-key-up #(reset! enddate (-> % .-target .-value))}]]]
     [:div
      [:button {:class "button is-primary is-small"
                :on-click #(js/alert (str [@id @meastype @startdate @enddate]))}
       "fetch"]]]))

(defn output-component
  []
  [:div
   [:h3 "output component"]])

(defn data-page []
  [:section.section>div.container>div.content
   (input-component)
   [:hr]
   (output-component)
   [:hr]])

;; -------------------------
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
  (GET "/api/users" {:handler #(reset! users %)}))

(defn fetch-measures! []
  (GET "/api/meas" {:handler #(reset! measures %)}))

(defn ^:dev/after-load mount-components []
  (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-users!)
  (fetch-measures!)
  (hook-browser-navigation!)
  (mount-components))
