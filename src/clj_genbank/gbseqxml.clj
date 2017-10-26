(in-ns 'clj-genbank.core)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; GBSeq XML
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftype gbseqReader [stream]
  ISeq
  (sequence-seq [this]
    (->> (xml/parse (.-stream this) :support-dtd false)
         :content
         (filter #(= (:tag %) :GBSeq))
         (map zip/xml-zip)))
  java.io.Closeable
  (close [this]
    (.close (.-stream this))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Some accessors
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn locus
  [gbz]
  (xzip/xml1-> gbz :GBSeq_locus xzip/text))

(defn primary-accession
  [gbz]
  (xzip/xml1-> gbz :GBSeq_primary-accession xzip/text))

(defn definition
  [gbz]
  (xzip/xml1-> gbz :GBSeq_definition xzip/text))

;; features

(defn features
  "Returns a collection of feature zippers."
  [gbz]
  (xzip/xml-> gbz :GBSeq_feature-table :GBFeature))

(defn feature-key
  "Returns the key of a feature zipper."
  [fz]
  (xzip/xml1-> fz :GBFeature_key xzip/text))

(defn feature-location
  "Returns the location from a feature zipper."
  [fz]
  (xzip/xml1-> fz :GBFeature_location xzip/text))

(defn feature-gene
  "Returns the gene name from a feature zipper."
  [fz]
  (xzip/xml1-> fz :GBFeature_quals :GBQualifier [:GBQualifier_name "gene"]
               :GBQualifier_value xzip/text))

(defn feature-protein-id
  "Returns the protein id from a feature zipper."
  [fz]
  (xzip/xml1-> fz :GBFeature_quals :GBQualifier [:GBQualifier_name "protein_id"]
               :GBQualifier_value xzip/text))

(defn feature-translation
  "Returns a protein translation from a feature zipper."
  [fz]
  (xzip/xml1-> fz :GBFeature_quals :GBQualifier [:GBQualifier_name "translation"]
               :GBQualifier_value xzip/text))

(defn feature-product
  "Returns the product of a feature zipper."
  [fz]
  (xzip/xml1-> fz :GBFeature_quals :GBQualifier [:GBQualifier_name "product"]
               :GBQualifier_value xzip/text))
