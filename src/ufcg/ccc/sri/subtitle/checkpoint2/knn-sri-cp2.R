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


queries <- read.csv("C:/Users/Brunna/Documents/queriesSRIlab2-cp2.csv", header=TRUE, sep=",") 
labels <- read.csv("C:/Users/Brunna/Documents/CategoriaFilmes.csv", header=TRUE, sep=",") 

queries.index = c()

train <- tfidf.matrix

for(i in 1:10){
  queries.index[i] <- queries[i,]$Index
  train <- train[-queries.index[i], ]
  labels <- labels[-queries.index[i],]
}

list.queries = list(tfidf.matrix[queries.index[1],], tfidf.matrix[queries.index[2],],tfidf.matrix[queries.index[3],],tfidf.matrix[queries.index[4],],tfidf.matrix[queries.index[5],],tfidf.matrix[queries.index[6],],tfidf.matrix[queries.index[7],],tfidf.matrix[queries.index[8],],tfidf.matrix[queries.index[9],],tfidf.matrix[queries.index[10],])
summary(list.queries)

test <- do.call(rbind, list.queries)

distance <- Distance_for_KNN_test(test, train)

knn <- knn_test_function(train, as.matrix(test), distance, labels[,2], k=30)

knn

