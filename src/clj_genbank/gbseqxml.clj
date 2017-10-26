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

(defn definition
  [gbz]
  (xzip/xml1-> gbz :GBSeq_definition xzip/text))

;; features

(defn features
  "Returns a list of feature zippers."
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

;; qualifiers

(defn qualifiers
  "Returns a list of qualifier zippers from a feature zipper."
  [fz]
  (xzip/xml-> fz :GBFeature_quals))

(defn qualifier-gene
  "Returns the gene name from a qualifier zipper."
  [qz]
  (xzip/xml1-> qz :GBQualifier [:GBQualifier_name "gene"] :GBQualifier_value xzip/text))

(defn qualifier-protein-id
  "Returns the protein id from a qualifier zipper."
  [qz]
  (xzip/xml1-> qz :GBQualifier [:GBQualifier_name "protein_id"] :GBQualifier_value xzip/text))

(defn qualifier-translation
  "Returns a protein translation from a qualifier zipper."
  [qz]
  (xzip/xml1-> qz :GBQualifier [:GBQualifier_name "translation"] :GBQualifier_value xzip/text))
