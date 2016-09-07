library(tm)
library(FastKNN)

path = "C:/legendasFinal/"


file.names <- dir(path)
doc.list = c()

for(i in 1:length(file.names)){
  filename <- paste(path, file.names[i], sep="")
  doc <- readChar(filename,file.info(filename)$size)
  doc.list[i] <- doc
}

query1 <- "Ace Ventura Pet Detective.DVDRip.BugBunny.br.srt"
query2 <- "Poltergeist(1982).br.srt"
query3 <- "Lord of the Rings The Two Towers The.DVDRip.SecretMyth.br.srt"
query4 <- "Fantastic Four.DVDRip.br.srt"
query5 <- "Frozen.720p.BlueRay.YIFY.br.srt"

my.docs <- VectorSource(c(doc.list, query1))
my.docs$Names <- c(names(doc.list), "legenda1")

my.corpus <- Corpus(my.docs)
my.corpus <- tm_map(my.corpus, removePunctuation)
my.corpus <- tm_map(my.corpus, stemDocument)
my.corpus <- tm_map(my.corpus, removeNumbers)
my.corpus <- tm_map(my.corpus, stripWhitespace)

term.doc.matrix.stm <- TermDocumentMatrix(my.corpus)
inspect(term.doc.matrix.stm)

term.doc.matrix <- as.matrix(term.doc.matrix.stm)

tfidf.matrix = weightTfIdf(term.doc.matrix.stm)
colnames(tfidf.matrix) <- colnames(term.doc.matrix)

train <- tfidf.matrix[1:646,]
test <- tfidf.matrix[647,]

distance <- Distance_for_KNN_test(test, train)

top5 <- k.nearest.neighbors(1,distance_matrix = distance, k=5)
top5



