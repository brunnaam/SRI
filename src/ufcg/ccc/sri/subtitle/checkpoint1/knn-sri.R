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

my.docs <- VectorSource(doc.list)
my.docs$Names <- names(doc.list)

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


queries <- read.csv("C:/Users/Brunna/Documents/queriesSRIlab2.csv", header=TRUE, sep=",") 

result.list = c()

for(i in 1:5){
  
  indexQuery <- queries[i,]$Index
  
  train <- tfidf.matrix[(i+1):length(doc.list),]
  test <- tfidf.matrix[1:(i-1),]
  
  distance <- Distance_for_KNN_test(test, train)
  
  top5 <- k.nearest.neighbors(1,distance_matrix = distance, k=5)
  result.list[i] <- paste(queries[i,]$Filme, paste(top5, collapse=', ' ), sep=": ")
  
}

result.list

