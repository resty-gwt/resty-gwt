class Group
  include ActiveModel::Serializers::JSON

  attr_accessor :id, :name

  def initialize(attributes = {})
    @id = attributes['id'].to_i > 0 ? attributes['id']: 987
    @name = attributes['name']
  end

  def attributes
    { 'id' => 987, 'name' => name }
  end
end
