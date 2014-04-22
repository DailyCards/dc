class CreateUsers < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.string :name
      t.string :email
      t.string :phoneno
      t.string :phoneid
      t.date :birtdate

      t.timestamps
    end
  end
end
