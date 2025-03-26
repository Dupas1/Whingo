import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.whingo.R

// Adapter para o RecyclerView que exibe as fotos
class PhotosAdapter(private val photos: List<Uri>) : RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>() {

    // ViewHolder para manter a referência ao ImageView dentro de cada item do RecyclerView
    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view_photo)
    }

    // Infla o layout de cada item (neste caso, um ImageView dentro do item_photo.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    // Vincula o URI da foto ao ImageView correspondente no ViewHolder
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUri = photos[position]

        // Tenta definir a imagem usando o URI selecionado
        holder.imageView.setImageURI(photoUri)

        // Se você quiser tratar o caso em que o URI é nulo ou inválido, você pode adicionar um log ou um comentário
        if (photoUri == null) {
            // Aqui você pode optar por não fazer nada ou definir uma imagem padrão
            // holder.imageView.setImageResource(R.drawable.placeholder_image) // Se você decidir adicionar um placeholder mais tarde
        }
    }


    // Retorna o tamanho da lista de fotos
    override fun getItemCount(): Int {
        return photos.size
    }
}
