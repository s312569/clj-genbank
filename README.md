# clj-genbank

A Genbank interface.

## Usage

In project.clj:

[clj-genbank "0.1.3"]

To search pubmed use `e-search`:

```clojure
clj-genbank.core> (take 3 (e-search "cyclotide" "protein"))
("1239396250" "1239396249" "1239396248")
clj-genbank.core>
```

To retrieve sequences in fasta or GBSeqXML formats use
`genbank-connection` in combination with `with-open` and
`sequence-seq`:

```clojure
clj-genbank.core> (with-open [r (genbank-connection (e-search "cyclotide" "protein")
                                                    "protein"
                                                    :fasta)]
                    (->> (sequence-seq r)
                         (take 3)
                         vec))
[{:accession "sp|C0HKK2.1|CYVNB_VIOIN", :description "RecName: Full=Cyclotide vinc-B",
 :sequence "GSIPACGESCFKGKCYTPGCTCSKYPLCAKN"} {:accession "sp|C0HKI3.1|CYMNA_MELAG",
 :description "RecName: Full=Cyclotide mang-A", :sequence "GFPTCGETCTLGTCNTPGCTCSWPICTRD"}
 {:accession "sp|C0HKJ3.1|CYMEK_MELDN", :description "RecName: Full=Cyclotide mden-K",
 :sequence "GSIPCGESCVWIPCISSVVGCACKNKVCYKN"}]
clj-genbank.core>
```

When using GBSeqXML format `sequence-seq` returns a lazy sequence of
sequences as xml zippers. Some accessors are defined:

```clojure
clj-genbank.core> (with-open [r (genbank-connection (e-search "cyclotide" "protein")
                                                    "protein"
                                                    :gb)]
                    (->> (sequence-seq r)
                         (take 3)
                         (mapv definition)))
["RecName: Full=Cyclotide vinc-B" "RecName: Full=Cyclotide mang-A"
 "RecName: Full=Cyclotide mden-K"]
clj-genbank.core> (with-open [r (genbank-connection (e-search "cyclotide" "nucleotide")
                                                    "nucleotide"
                                                    :gb)]
                    (->> (sequence-seq r)
                         (take 3)
                         (mapcat features)
                         (filter #(= (feature-key %) "CDS"))
                         (mapv feature-protein-id)))
["ASR19270.1"]
clj-genbank.core>
```

## License

Copyright © 2017 Jason Mulvenna

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
