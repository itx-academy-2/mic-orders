Feature: Password Reset Background Variables

  Background:
    * def urls = { retailApiUrl: 'http://localhost:8080/retail' }
    * def email = 'user@mail.com'
    * def retailBasePath = '/v1/password-reset'
    * def validToken = '11111111-1111-1111-1111-111111111111'
    * def newPassword = 'StrongPass1!new'
    * def resetEmailRequest = read('classpath:apis/orders/test-data/requests/password-reset-email.json')
    * def resetEmailBadRequest = read('classpath:apis/orders/test-data/requests/password-reset-email-bad.json')
    * def resetPasswordRequest = read('classpath:apis/orders/test-data/requests/password-reset-valid-token.json')
    * def resetPasswordInvalidTokenRequest = read('classpath:apis/orders/test-data/requests/password-reset-invalid-token.json')

  Scenario: dummy
    * def result = { urls: urls, email: email, retailBasePath: retailBasePath, validToken: validToken, newPassword: newPassword, resetEmailRequest: resetEmailRequest, resetEmailBadRequest: resetEmailBadRequest, resetPasswordRequest: resetPasswordRequest, resetPasswordInvalidTokenRequest: resetPasswordInvalidTokenRequest }
    * karate.set('vars', result)

