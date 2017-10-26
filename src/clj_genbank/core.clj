(ns clj-genbank.core
  (:require [clj-http.client :as client]
            [clojure.data.xml :as xml]
            [clojure.data.zip.xml :as xzip]
            [clojure.zip :as zip]
            [clojure.string :refer [split]]
            [clj-fasta.core :as fa]
            [clojure.java.io :as io]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; protocols
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol ISeq
  (sequence-seq [r]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; fasta reader
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftype gbfastaReader [stream]
  ISeq
  (sequence-seq [this]
    (fa/fasta-seq (.-stream this)))
  java.io.Closeable
  (close [this]
    (.close (.-stream this))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; load files
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(load "gbseqxml")
(load "eutilities")
