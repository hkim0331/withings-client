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

(def ^:private version "0.4.10")

(defonce session (r/atom {:page :home
                          :name nil
                          :cid nil
                          :secret nil
                          :belong nil
                          :email nil}))
(defonce users (r/atom {}))

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
       [nav-link "#/about" "About" :about]
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
;; edit page

(defn edit-user-page
  [id]
  [:section.section>div.container>div.content
   [:div
    [:h2 "Edit"]
    [:p "id =" id]]])

;; -------------------------
;; home page
(def redirect-uri js/redirectUrl)
;; (def redirect-uri "https://wc.melt.kyutech.ac.jp/callback")

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
    "create ボタンの後、下に現れるリンクをクリックし、"
    "acccess トークン、refresh トークンを取得する。"
    "ページが切り替わるのに 5 秒くらいかかる。"]
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
     [:div {:class "columns"}
      [:div {:class "column"} (if (:valid user) "y" "n")]
      [:div {:class "column"} (:id user)]
      [:div {:class "column"} (:name user)]
      [:div {:class "column"} (:belong user)]
      [:div {:class "column"} (:email user)]
      [:div {:class "column"} (tm (:updated_at user))]
      [:div {:class "column"}
       [:button {:on-click
                 (fn [_] (POST "/api/token/refresh"
                           {:format :json
                            :headers
                            {"Accept" "application/transit+json"
                             "x-csrf-token" js/csrfToken}
                            :params user
                            :handler
                            #(js/alert (str "/api/token/refresh " %))
                            :error-handler
                            #(js/alert (str "error /api/token/refresh " %))}))}
        "refresh"]]
      [:div {:class "column"}
       [:button {:on-click #(swap! session assoc :page :edit)} "edit"]]])])

(defn home-page []
  [:section.section>div.container>div.content
   (new-component)
   [:br]
   (link-component)
   [:hr]
   (users-component)
   [:hr]
   version])

;; -------------------------
(def pages
  {:home #'home-page
   :about #'about-page
   :edit #'edit-user-page})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :home]
    ["/about" :about]]))

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

(defn ^:dev/after-load mount-components []
  (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-users!)
  (hook-browser-navigation!)
  (mount-components))
