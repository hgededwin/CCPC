package com.cacaopaycard.cacaopay.AdminCuenta;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class PerfilFragment extends Fragment implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtCambiarFoto, txtCancelar, txtTitular;
    private TextInputEditText edtxtTitular, edtxtCorreo, edtxtFecha, edtxtTelefono;
    private ImageView imgInfoCambio;
    private Button btnGuardarCambios;
    private CircleImageView fotoPerfil;
    private final static int INDEX_CAMERA = 0;
    private final static int INDEX_GALERIA = 1;
    private boolean esEditable = false;
    private Usuario usuario;

    private int month = Calendar.getInstance().get(Calendar.MONTH);
    private int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private int year = Calendar.getInstance().get(Calendar.YEAR);

    private View view;

    public PerfilFragment() {
        // Required empty public constructor
    }

    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
  /*          mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //toolbar = view.findViewById(R.id.toolbar_perfil);
        usuario = new Usuario(view.getContext());
        txtCambiarFoto = view.findViewById(R.id.txt_cambiar_foto);
        fotoPerfil = view.findViewById(R.id.img_perfil);

        edtxtTitular =  view.findViewById(R.id.edtxt_titular_perfil);
        txtTitular = view.findViewById(R.id.txt_nombre_titulo);
        edtxtTelefono = view.findViewById(R.id.edxt_telefono_perfil);
        edtxtCorreo = view.findViewById(R.id.edtxt_correo_perfil);
        txtCambiarFoto = view.findViewById(R.id.txt_cambiar_foto);
        edtxtFecha = view.findViewById(R.id.edtxt_fecha_perfil);
        btnGuardarCambios = view.findViewById(R.id.btn_guardar_perfil);
        txtCancelar = view.findViewById(R.id.btn_cancelar_perfil);
        imgInfoCambio = view.findViewById(R.id.img_alert_info_edit);

        String strNombre = usuario.getNombreUsuario().isEmpty() ? "José Pérez" : usuario.getNombreUsuario();
        String strCorreo = usuario.getCorreo().isEmpty() ? "cacao@cacaopay.com" : usuario.getCorreo();
        String strTelefono = usuario.getTelefono().isEmpty() ? "5512933445" : usuario.getTelefono();

        edtxtTitular.setText(strNombre);
        txtTitular.setText(strNombre);
        edtxtTelefono.setText(strTelefono);
        edtxtCorreo.setText(strCorreo);
        edtxtFecha.setText("12/12/12");

        //edtxtFecha.setOnClickListener(this);
        btnGuardarCambios.setOnClickListener(this);
        txtCancelar.setOnClickListener(this);
        txtCambiarFoto.setOnClickListener(this);
        imgInfoCambio.setOnClickListener(this);

        return view;
    }



    public void onClickCambiarFoto(View view) {

        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                //--------Comentado temporalmente-------//
                //--------------------------------------//
                if (index == INDEX_CAMERA)
                {
                    System.out.println("Cámara");
                    Intent gallery = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(gallery, INDEX_CAMERA);
                    dialog.dismiss();
                }
                else {
                    System.out.println("Galería");
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, INDEX_GALERIA);
                    dialog.dismiss();
                }
            }
        });
        final int paddiginIcon = 5;
        adapter.add(new MaterialSimpleListItem.Builder(view.getContext())
                .content(R.string.str_tomar_foto)
                .icon(R.drawable.ic_camera)
                .iconPaddingDp(paddiginIcon)
                .backgroundColor(getResources().getColor(R.color.whitePrimary))
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(view.getContext())
                .content(R.string.str_elegir_galeria)
                .icon(R.drawable.ic_galeria)
                .iconPaddingDp(paddiginIcon)
                .backgroundColor(getResources().getColor(R.color.whitePrimary))
                .build());
        MaterialDialog dialog = new MaterialDialog.Builder(view.getContext())
                .adapter(adapter, null)
                .show();
    }

    private File bitmapToFile(Bitmap bitmap){

        File filesDir = view.getContext().getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, "foto" + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }

    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK && requestCode == INDEX_GALERIA){
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};


            Cursor cursor = view.getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String filePath = cursor.getString(columnIndex);
            cursor.close();

            File file = new File(filePath);
            file = saveBitmapToFile(file);

            Bitmap bitmap =  BitmapFactory.decodeFile(filePath);
            fotoPerfil.setImageBitmap(bitmap);


        } else if(resultCode == RESULT_OK && requestCode == INDEX_CAMERA){
            final Bitmap bitmap = data.getParcelableExtra("data");
            File file = bitmapToFile(bitmap);

            fotoPerfil.setImageBitmap(bitmap);


        }
    }

    public void onClickCancelarEditarPerfil(View view) {
        updateViews(esEditable);

    }


    public void onClickGuardarCambiosPerfil(View view) {
        updateViews(esEditable);

    }

    public void updateViews(boolean editable){
        edtxtTitular.setEnabled(!editable);
        edtxtTitular.setFocusableInTouchMode(!editable);

        edtxtTelefono.setEnabled(!editable);
        edtxtTelefono.setFocusableInTouchMode(!editable);

        edtxtCorreo.setEnabled(!editable);
        edtxtCorreo.setFocusableInTouchMode(!editable);

        edtxtFecha.setEnabled(!editable);

        if(editable) {
            txtCancelar.setVisibility(View.GONE);
            btnGuardarCambios.setText(R.string.str_editar_informacion);
        }else {
            txtCancelar.setVisibility(View.VISIBLE);
            btnGuardarCambios.setText(R.string.str_guardar);

        }

        esEditable = !editable;
    }

    public void onClickSetDate(View view) {
        DatePickerDialog fecha = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? "0" + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? "0" + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                edtxtFecha.setText(diaFormateado + "/" + mesFormateado + "/" + year);

            }
        },year,month,day);

        fecha.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.edtxt_fecha_perfil:
                break;
            case R.id.txt_cambiar_foto:
                //onClickCambiarFoto(txtCambiarFoto);
                new MaterialDialog.Builder(view.getContext())
                        .title(getResources().getString(R.string.app_name))
                        .positiveText("OK")
                        .content("Contáctanos a través de la sección de ayuda para editar tu información.")
                        .show();
                break;
            case R.id.btn_cancelar_perfil:
                onClickCancelarEditarPerfil(txtCancelar);
                break;
            case R.id.btn_guardar_perfil:
                onClickGuardarCambiosPerfil(btnGuardarCambios);
                break;
            case R.id.img_alert_info_edit:
                new MaterialDialog.Builder(view.getContext())
                        .title(getResources().getString(R.string.app_name))
                        .positiveText("OK")
                        .content("Contáctanos a través de la sección de ayuda para editar tu información.")
                        .show();
                break;
        }

    }
}
