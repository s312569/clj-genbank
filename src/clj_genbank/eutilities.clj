(in-ns 'clj-genbank.core)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; network
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def bioseq-proxy (atom {}))

(defn set-bioseq-proxy!
  [params]
  (reset! bioseq-proxy params))

(defn get-req
  ([a] (get-req a {}))
  ([a param]
   (client/get a (merge param {:cookie-policy :standard} @bioseq-proxy))))

(defn post-req
  [a param]
  (client/post a (merge param {:cookie-policy :standard} @bioseq-proxy)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; api
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- search-url
  ([term db retstart] (search-url term db retstart nil))
  ([term db retstart key]
     (xml/parse-str
      (:body
       (get-req
        (str "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db="
             (java.net.URLEncoder/encode (name db))
             "&term="
             (java.net.URLEncoder/encode term)
             "&retmax=" 1000
             "&retstart=" retstart
             (if key (str "&WebEnv=" key))
             "&usehistory=y"))))))

(defn e-search
  "Takes a term and a database and returns a list of accession numbers
  matching the search term."
  ([term db] (e-search term db 0 nil))
  ([term db restart key]
     (let [r (search-url term db restart key)
           k (xml1-> (xml-zip r) :WebEnv text)
           c (Integer/parseInt (xml1-> (xml-zip r) :Count text))]
       (if (> restart c)
         nil
         (lazy-cat (xml-> (xml-zip r) :IdList :Id text)
                   (e-search term db (+ restart 1000) k))))))

(defn genbank-connection
  "Retrieves a stream of database entries from GenBank corresponding
  to the list of accession numbers. The :rettype keyword can be
  either :fasta or :gb, for fasta and GBSeq XML formats, returning a
  gbfastaReader or gbSeqReader respectively."
  [a-list db rettype]
  (if (empty? a-list)
    nil
    (let [r (post-req "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi"
                         {:query-params
                          (merge {:db (name db)
                                  :id (apply str (interpose "," a-list))}
                                 (condp = rettype
                                   :fasta {:rettype "fasta" :retmode "text"}
                                   :gb {:rettype "gb" :retmode "xml"}
                                   (throw (Exception.
                                           (str "Rettype " rettype " not recognised.")))))
                          :as :stream})]
      (condp = rettype
        :gb (->gbseqReader (io/reader (:body r)))
        :fasta (->gbfastaReader (io/reader (:body r)))))))

(defn e-link
  [acc idb tdb]
  (let [x (xml/parse-str
           (:body (post-req "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi"
                            {:query-params (merge {:dbfrom (name idb)
                                                   :db (name tdb)
                                                   :id acc})})))
        p (xml-> (xml-zip x) :LinkSet :LinkSetDb)]
    (into {}
          (map #(vector (xml1-> % :LinkName text)
                        (xml-> % :Link :Id text))
               p))))
