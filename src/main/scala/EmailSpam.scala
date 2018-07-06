import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}

object EmailSpam extends App{

  val conf = new SparkConf().setAppName("email-spam").setMaster("local[*]")
  val sc = new SparkContext(conf)

  val spam = sc.textFile("./training_data/spam/0052.2003-12-20.GP.spam.txt", 4)
  val normal = sc.textFile("/training_data/ham/0022.1999-12-16.farmer.ham.txt", 4)

  val tf = new HashingTF(numFeatures = 10000)

  val spamFeature = spam.map(email => tf.transform(email.split(" ")))
  val hamFeature = normal.map(email => tf.transform(email.split(" ")))

  val positiveSpam = spamFeature.map(feature => LabeledPoint(1, feature))
  val negativeSpam = hamFeature.map(feature => LabeledPoint(0, feature))

  val trainingData = positiveSpam union negativeSpam

  val modelinit = new LinearRegressionWithSGD()
  val model = modelinit.run(trainingData)

  val positiveTest = tf.transform("insurance plan for you")
  val negativeTest = tf.transform("i'll be late there")

  println("Positive test result : " + model.predict(positiveTest))
  println("Negative test result : " + model.predict(negativeTest))
}
