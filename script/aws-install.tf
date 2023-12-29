
# Створити terraform script:
# - у мене є лямбда функція з назвою `movie-telegram-bot-tf` 
# і головним класом `org.ovelychko.awsbotsystem.MainApplication::handleRequest`
# - ця лямбда отримує реквести з Api Gateway, API type: HTTP, Resource path: /movie-telegram-bot-tf
# - до цієї лямбди треба підключити CloudWatch Log group з Retention 3 days

# AWS CLI:
# brew install awscli
# aws configure --profile your_aws_profile

# Terraform commands:
# brew install terraform
# terraform init
# terraform apply
# terraform destroy

provider "aws" {
  region = "eu-west-3"
}

resource "aws_lambda_function" "movie_telegram_bot_tf" {
  function_name    = "movie-telegram-bot-tf"
  handler          = "org.ovelychko.awsbotsystem.MainApplication::handleRequest"
  runtime          = "java21"
  memory_size      = 256
  timeout          = 30
  filename         = "../movie-telegram-bot/build/distributions/movie-telegram-bot-v1.0.zip"
  role = aws_iam_role.lambda_exec.arn

  environment {
    variables = {
      # Додайте будь-які змінні середовища, які потрібні вашій лямбді
    }
  }

  depends_on = [aws_iam_role.lambda_exec]
}

resource "aws_iam_role" "lambda_exec" {
  name = "lambda_exec_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "lambda.amazonaws.com",
        },
      },
    ],
  })
}

resource "aws_apigatewayv2_api" "api" {
  name          = "movie-telegram-bot-tf-api"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_integration" "lambda_integration" {
  api_id             = aws_apigatewayv2_api.api.id
  integration_type  = "AWS_PROXY"
  integration_uri   = aws_lambda_function.movie_telegram_bot_tf.invoke_arn
  integration_method = "POST"
  connection_type   = "INTERNET"
  timeout_milliseconds = 30000
}

resource "aws_apigatewayv2_route" "route" {
  api_id    = aws_apigatewayv2_api.api.id
  route_key = "POST /movie-telegram-bot-tf"

  target = "integrations/${aws_apigatewayv2_integration.lambda_integration.id}"
}

resource "aws_apigatewayv2_stage" "stage" {
  api_id = aws_apigatewayv2_api.api.id
  name   = "movie-telegram-bot-tf"
  auto_deploy = true
}

resource "aws_lambda_permission" "apigateway" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.movie_telegram_bot_tf.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn = "${aws_apigatewayv2_api.api.execution_arn}/*"
}

resource "aws_lambda_permission" "cloudwatch_logs" {
  statement_id  = "AllowLoggingToCloudWatchLogs"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.movie_telegram_bot_tf.function_name
  principal     = "logs.amazonaws.com"
}

resource "aws_cloudwatch_log_group" "log_group" {
  name              = "/aws/lambda/movie-telegram-bot-tf"
  retention_in_days = 3
}
