class ApplicationController < ActionController::Base
  protect_from_forgery
  
  private

  after_filter :csrf

  def csrf
    response.header['X-CSRF-Token'] = form_authenticity_token
  end
end
