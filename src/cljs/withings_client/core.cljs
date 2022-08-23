(ns withings-client.core
  (:require
   [ajax.core :refer [GET POST]]
   [cljs.core.async :refer [<!]]
   [clojure.string :as string]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [markdown.core :refer [md->html]]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reitit.core :as reitit]
   [withings-client.ajax :as ajax])
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:import
   goog.History))

(defonce session (r/atom {:page :home}))

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
       [nav-link "#/about" "About" :about]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]
   ;; test csrf-token
   [:p js/csrfToken]])

;;
(def redirect-uri "https://wc.melt.kyutech.ac.jp/callback")
(def withings-uri "https://account.withings.com/oauth2_user/authorize2")
(def scope "user.metrics,user.activity,user.info")
(def base
  (str withings-uri
       "?response_type=code&redirect_uri=" redirect-uri "&"
       "scope=" scope "&"))

;; こっちだと CORS に抵触
;; (defn call-withings []
;;   (let [{:keys [name cid belong email]} @session]
;;     (.log js/console name cid belong email)
;;     (GET withings-uri {:params {:response_type "code"
;;                                 :client_id cid
;;                                 :scope "user.metrics,user.activity"
;;                                 :redirect_uri redirect-uri
;;                                 :state name}
;;                        :handler #(.log js/console %)
;;                        :error-handler #(.log js/console (str "error" %))})))

(defn create-url
  []
  (str base "client_id=" (:cid @session) "&state=" (:name @session)))

(defn new-component []
  [:div
   [:h2 "new"]
   [:div [:label {:class "label"} "name"]]
   [:div {:class "field"}
    [:input {:on-change #(swap! session
                                assoc
                                :name
                                (-> % .-target .-value))}]]
   [:div [:label {:class "label"} "cid"]]
   [:div {:class "field"}
    [:input {:on-change #(swap! session
                                assoc
                                :cid
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
              :on-click #(swap! session
                                assoc
                                :uri (create-url))}
             "create"]]])

(defn link-component []
  [:div
   [:p "create ボタンの後、下に現れるリンクをクリック。" [:br]
    "create のタイミングで name, belong, email を DB インサートするため、"
    "create を省略できない。"]
   [:p "クリックで登録 → " [:a {:href (:uri @session)} (:name @session)]]])

(defn users-component []
  [:div
   [:h2 "users"]])

(defn home-page []
  [:section.section>div.container>div.content
   (new-component)
   [:br]
   (link-component)
   [:br]
   (users-component)])

(def pages
  {:home #'home-page
   :about #'about-page})

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
;; (defn fetch-docs! []
;;  (GET "/docs" {:handler #(swap! session assoc :docs %)}))

(defn ^:dev/after-load mount-components []
  (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  ;; (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
