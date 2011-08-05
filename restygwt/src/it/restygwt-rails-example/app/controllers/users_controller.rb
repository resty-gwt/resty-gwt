class UsersController < ApplicationController

  def index
    render :json => [session[:user]].to_json
  end

  def show
    render :json => session[:user]
  end

  def create
     user = User.new(params[:user])
     user.groups << Group.new('name' => "root")
     session[:user] = user

     render :json => user
   end

   def update
     if session[:user] && params[:id] == session[:user].id.to_s
      
       render :json => session[:user]
     else
       head :not_found
     end
   end

end
