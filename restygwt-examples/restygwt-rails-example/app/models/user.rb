#
# Copyright (C) 2009-2012 the original author or authors.
# See the notice.md file distributed with this work for additional
# information regarding copyright ownership.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

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
