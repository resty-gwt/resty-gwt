class User
  include ActiveModel::Serializers::JSON
  
  attr_accessor :id, :name, :groups

  def attributes
    {'id' => 123, 'name' => name, 'groups' => groups.collect { |g| g.attributes } }
  end

  def initialize(attributes = {})
    @id =  attributes['id'].to_i > 0 ? attributes['id']: 123
    @name = attributes['name']
    @groups = (attributes['groups'] || []).collect {|g| Group.new g }
  end
end
